package com.github.mtdhllf.kit.coroutines

/**
 * This is [Run.onBackground] and [Run.onUiAsync] (Action)} result class
 * In this you can check done and cancel the asynchronous task
 */

interface Result {

    val isDone: Boolean

    fun cancel()

}