package com.godq.threadpool

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.LockSupport
import kotlin.random.Random

class TaskScheduler : Executor {

    companion object {

        private val NOT_IN_STACK = Any()

        // Worker ctl states
        private const val PARKED = -1
        private const val CLAIMED = 0
        private const val TERMINATED = 1

        private const val IDLE_WORKER_KEEP_ALIVE_NS = 10_000_000000

        private const val MAX_POOL_SIZE = 5

        // Masks of control state
        private const val BLOCKING_SHIFT = 21 // 2M threads max
        private const val CREATED_MASK: Long = (1L shl BLOCKING_SHIFT) - 1
        private const val BLOCKING_MASK: Long = CREATED_MASK shl BLOCKING_SHIFT
        private const val CPU_PERMITS_SHIFT = BLOCKING_SHIFT * 2
        private const val CPU_PERMITS_MASK = CREATED_MASK shl CPU_PERMITS_SHIFT


    }

//    private val corePoolSize = Runtime.getRuntime().availableProcessors()
    private val corePoolSize = 3

    private val controlState = AtomicLong(corePoolSize.toLong() shl CPU_PERMITS_SHIFT)

    private fun createdWorkers(state: Long): Int = (state and CREATED_MASK).toInt()
    // Guarded by synchronization
    private fun incrementCreatedWorkers(): Int = createdWorkers(controlState.incrementAndGet())
    private fun decrementCreatedWorkers(): Int = createdWorkers(controlState.getAndDecrement())

    private fun blockingTasks(state: Long): Int = (state and BLOCKING_MASK shr BLOCKING_SHIFT).toInt()
    private fun incrementBlockingTasks() = controlState.addAndGet(1L shl BLOCKING_SHIFT)
    private fun decrementBlockingTasks() = controlState.addAndGet(-(1L shl BLOCKING_SHIFT))

    private fun availableCpuPermits(state: Long): Int = (state and CPU_PERMITS_MASK shr CPU_PERMITS_SHIFT).toInt()
    private fun releaseCpuPermit() = controlState.addAndGet(1L shl CPU_PERMITS_SHIFT)


    private fun tryAcquireCpuPermit(): Boolean {
        while (true) {
            val state = controlState.get()
            val available = availableCpuPermits(state)
            if (available == 0) return false
            val update = state - (1L shl CPU_PERMITS_SHIFT)
            if (controlState.compareAndSet(state, update)) return true
        }
    }

    val globalCpuQueue = ConcurrentLinkedQueue<Task>()

    val globalBlockingQueue = ConcurrentLinkedQueue<Task>()

    val workers = arrayOfNulls<Worker>(MAX_POOL_SIZE + 1)

    var parkedWorkerStackTopIndex = AtomicInteger(0)

    override fun execute(command: Runnable) = dispatch(command)

    private fun dispatch(command: Runnable) {
        val task = command as? Task ?: TaskImpl(TASK_MODE_IO, command)

        addToGlobalQueue(task)

        if (task.mode == TASK_MODE_DEFAULT) {
            signalCpuWork()
        } else {
            signalBlockingWork()
        }
    }

    private fun addToGlobalQueue(task: Task): Boolean {
        return if (task.mode == TASK_MODE_DEFAULT) {
            globalCpuQueue.offer(task)
        } else {
            globalBlockingQueue.offer(task)
        }
    }

    private fun signalCpuWork() {
        if (tryUnPark()) return
        if (createWorker(taskType = 0)) return
        tryUnPark()
    }

    private fun signalBlockingWork() {
        val stateSnapShot = incrementBlockingTasks()
        if (tryUnPark()) return
        if (createWorker(state = stateSnapShot, taskType = 1)) return
        tryUnPark()
    }

    private fun createWorker(state: Long = controlState.get(), taskType: Int): Boolean {
        synchronized(workers) {

            val created = createdWorkers(state)
            val blocking = blockingTasks(state)
            val cpuWorkers = (created - blocking).coerceAtLeast(0)
            if (cpuWorkers >= corePoolSize) return false

            if (created >= MAX_POOL_SIZE) return false

            val task = (if (taskType == 0) globalCpuQueue.poll() else globalBlockingQueue.poll()) ?: return false

            val createdWorkers = incrementCreatedWorkers()
            workers[createdWorkers] = Worker(createdWorkers)
            workers[createdWorkers]?.initTask = task
            workers[createdWorkers]?.start()
            return true
        }
    }

    private fun tryUnPark(): Boolean {
        while (true) {

            val worker = parkedWorkersStackPop() ?: return false

            return worker.unPark()
        }

    }

    private fun parkedWorkersStackPop(): Worker? {
        while (true) {
            val value = parkedWorkerStackTopIndex.get()
            val worker = workers[value] ?: return null
            val updIndex = parkedWorkersStackNextIndex(worker)
            if (updIndex < 0) continue
            if (parkedWorkerStackTopIndex.compareAndSet(value, updIndex)) {
                worker.nextParkedWork = NOT_IN_STACK
                return worker
            }
        }
    }


    override fun toString(): String {
        return String.format("\n\n--------\ncpu core: %d\n" +
                "createdWorkers:%d\n" +
                "blockingTasks:%d\n" +
                "availableCpuPermits: %d\n" +

                "--------\n\n",
            corePoolSize,
            createdWorkers(controlState.get()),
            blockingTasks(controlState.get()),
            availableCpuPermits(controlState.get())
        )
    }


    fun getCurrentSchedulerInfo(): String {
        synchronized(workers) {
            var info = ""
            for (worker in workers) {
                info += "[${worker?.name ?: "null"}]  "
            }

            info += "\n parked work \n"

            var index = parkedWorkerStackTopIndex.get()
            while (true) {
                val worker = workers[index] ?: break
                info += "[${worker.name ?: "null"}]  "
                val next = worker.nextParkedWork as? Worker ?: break

                index = next.indexInArray
            }
            return info + "\n$this\n"
        }
    }

    private fun parkedWorkersStackNextIndex(worker: Worker): Int {
        var next = worker.nextParkedWork
        findNext@ while (true) {
            when {
                next === NOT_IN_STACK -> return -1 // we are too late -- other thread popped this element, retry
                next === null -> return 0 // stack becomes empty
                else -> {
                    val nextWorker = next as Worker
                    val updIndex = nextWorker.indexInArray
                    if (updIndex != 0) return updIndex // found good index for next worker
                    // Otherwise, this worker was terminated and we cannot put it to top anymore, check next
                    next = nextWorker.nextParkedWork
                }
            }
        }
    }

    internal fun parkedWorkersStackTopUpdate(worker: Worker, oldIndex: Int, newIndex: Int) {

        while (true) {
            val index = parkedWorkerStackTopIndex.get()
            val updIndex = if (index == oldIndex) {
                if (newIndex == 0) {
                    parkedWorkersStackNextIndex(worker)
                } else {
                    newIndex
                }
            } else {
                index
            }

            if (updIndex < 0) continue

            if (parkedWorkerStackTopIndex.compareAndSet(index, updIndex)) return
        }
    }

    inner class Worker(var indexInArray: Int): Thread() {

        var nextParkedWork: Any? = null

        private var exeTaskCountRecord = 0

        var initTask: Task? = null

        //updated only by this worker thread
        private var state = WorkerState.DORMANT

        init {
            name = "thread pool worker-$indexInArray  [${this.hashCode()}]"
        }

        private var workerCtrl = AtomicInteger(CLAIMED)

        override fun run() = runTask()

        private fun runTask() {
            while (state != WorkerState.TERMINATED) {
                val task = findAnyTask()
                if (task != null) {
                    executeTask(task)
                    exeTaskCountRecord++
                    continue
                }
                tryPark()

                terminate()

            }
        }

        private fun executeTask(task: Task) {
            val taskMode = task.mode
            beforeRun(taskMode)
            runSafely(task)
            afterRun(taskMode)
        }

        private fun beforeRun(taskMode: Int) {
            if (taskMode == TASK_MODE_DEFAULT) return
            // Always notify about new work when releasing CPU-permit to execute some blocking task
            if (tryReleaseCpu(WorkerState.BLOCKING)) {
                signalCpuWork()
            }
        }

        private fun runSafely(task: Task) {
            try {
                task.run()
            } catch (e: Throwable) {

            }
        }

        private fun afterRun(taskMode: Int) {
            if (taskMode == TASK_MODE_DEFAULT) return
            decrementBlockingTasks()
            state = WorkerState.DORMANT
        }

        /**
         * 只有当前状态为 WorkerState.PARKING 的work会触发terminate
         * */
        private fun terminate() {

            if (!workerCtrl.compareAndSet(PARKED, TERMINATED)) return

            synchronized(workers) {
                val oldIndex = indexInArray
                indexInArray = 0
                parkedWorkersStackTopUpdate(this, oldIndex, 0)

                val lastIndex = decrementCreatedWorkers()
                val lastWorker = workers[lastIndex]!!
                workers[oldIndex] = lastWorker
                lastWorker.indexInArray = oldIndex

                parkedWorkersStackTopUpdate(lastWorker, lastIndex, oldIndex)

                workers[lastIndex] = null

            }

            state = WorkerState.TERMINATED

        }

        /**
         * This method is invoked only from the worker thread itself.
         *
         * */
        private fun tryPark() {
            while (true) {
                val index = parkedWorkerStackTopIndex.get()
                val newIndex = indexInArray
                val topWorker = workers[index]
                nextParkedWork = topWorker
                workerCtrl.set(PARKED) //safe
                if (parkedWorkerStackTopIndex.compareAndSet(index, newIndex)) {
                    tryReleaseCpu(WorkerState.PARKING)
                    interrupted()
                    LockSupport.parkNanos(IDLE_WORKER_KEEP_ALIVE_NS)
                    return
                }
            }
        }

        /**
         * 可能正在触发terminate，允许park失败
         * */
        fun unPark(): Boolean {
            if (workerCtrl.compareAndSet(PARKED, CLAIMED)) {
                nextParkedWork = null
                interrupted()
                LockSupport.unpark(this)
                return true
            }
            return false
        }

        private fun findLocalTask() : Task? {
            val task = initTask ?: return null
            initTask = null
            return task
        }

        private fun findAnyTask(): Task? {
            val task = findLocalTask()
            if (tryAcquireCpuPermit()) {
                if (task != null) {
                    return task
                }
                return if (Random.nextInt(2) == 0) (globalCpuQueue.poll() ?: globalBlockingQueue.poll()) else (globalBlockingQueue.poll() ?: globalCpuQueue.poll())
            }
            return if (task != null) {
                if (task.mode == TASK_MODE_DEFAULT) {
                    globalCpuQueue.offer(task)
                    signalCpuWork()
                    null
                } else {
                    task
                }
            } else {
                globalBlockingQueue.poll()
            }
        }

        private fun tryReleaseCpu(newState: WorkerState): Boolean {
            val previousState = state
            val hadCpu = previousState == WorkerState.CPU_ACQUIRED
            if (hadCpu) releaseCpuPermit()
            if (previousState != newState) state = newState
            return hadCpu
        }

        private fun tryAcquireCpuPermit(): Boolean = when {
            state == WorkerState.CPU_ACQUIRED -> true
            this@TaskScheduler.tryAcquireCpuPermit() -> {
                state = WorkerState.CPU_ACQUIRED
                true
            }
            else -> false
        }

    }

    enum class WorkerState {
        /**
         * Has CPU token and either executes TASK_NON_BLOCKING task or tries to find one.
         */
        CPU_ACQUIRED,

        /**
         * Executing task with TASK_PROBABLY_BLOCKING.
         */
        BLOCKING,

        /**
         * Currently parked.
         */
        PARKING,

        /**
         * Tries to execute its local work and then goes to infinite sleep as no longer needed worker.
         */
        DORMANT,

        /**
         * Terminal state, will no longer be used
         */
        TERMINATED
    }
}