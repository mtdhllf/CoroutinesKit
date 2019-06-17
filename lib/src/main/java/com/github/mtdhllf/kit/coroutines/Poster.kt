package com.github.mtdhllf.kit.coroutines

/**
 * This is common poster to run same work by async or sync
 */
internal interface Poster {

    /**
     * Add a async post to Handler pool
     *
     * @param runnable Runnable
     */
    fun async(runnable: Task)

    /**
     * Add a async post to Handler pool
     *
     * @param runnable Runnable
     */
    fun sync(runnable: Task)


    /**
     * To dispose the resource
     */
    fun dispose()

    companion object {
        const val ASYNC = 0x10101010
        const val SYNC = 0x20202020
    }
}