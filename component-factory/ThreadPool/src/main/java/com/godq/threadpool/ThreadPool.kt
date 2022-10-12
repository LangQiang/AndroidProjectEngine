package com.godq.threadpool

class ThreadPool {

    companion object {
        private val scheduler: TaskScheduler by lazy {
            TaskScheduler()
        }
        fun exec(mode: Int = TASK_MODE_IO, block: () -> Unit) {
            scheduler.execute(object : Task(mode) {
                override fun run() {
                    block.invoke()
                }
            })
        }

        @JvmStatic
        fun exec(mode: Int = TASK_MODE_IO, runnable: Runnable) {
            scheduler.execute(object : Task(mode) {
                override fun run() {
                    runnable.run()
                }
            })
        }

        @JvmStatic
        fun exec(runnable: Runnable) {
            exec(TASK_MODE_IO, runnable)
        }

        fun getCurrentSchedulerInfo(): String = scheduler.getCurrentSchedulerInfo()
    }
}