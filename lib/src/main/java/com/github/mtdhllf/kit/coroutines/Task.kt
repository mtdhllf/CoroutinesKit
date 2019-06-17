package com.github.mtdhllf.kit.coroutines

import java.util.Queue

/**
 * The Task be used to Poster do something
 * This Task extends [Runnable] and [Result]
 */

interface Task : Runnable, Result {

    fun setPool(pool: Queue<Task>)
    
}