package com.godq.threadpool


const val TASK_MODE_IO = 0
const val TASK_MODE_DEFAULT = 1

abstract class Task(var mode: Int) : Runnable

class TaskImpl(mode: Int, var block: Runnable): Task(mode) {
    override fun run() {
        block.run()
    }

}