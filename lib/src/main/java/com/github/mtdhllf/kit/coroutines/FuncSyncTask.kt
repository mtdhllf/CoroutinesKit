package com.github.mtdhllf.kit.coroutines

import java.util.Queue
import java.lang.Runnable

/**
 * FuncSyncTask use to [Func] and [Runnable]
 * See [Run] call this
 */
internal class FuncSyncTask<T>
/**
 * In this we call cal the [Func]
 * and check should run it
 *
 * @return T
 */
    (private val mFunc: Func<T>) : Func<T>, Task {
    private var mResult: T? = null
    override var isDone = false
    private var mPool: Queue<Task>? = null

    private val lock = java.lang.Object()


    override fun call(): T {
        // Cleanup reference the pool
        mPool = null
        // Doing
        return mFunc.call()
    }

    /**
     * Run to doing something
     */
    override fun run() {
        if (!isDone) {
            synchronized(lock) {
                if (!isDone) {
                    mResult = call()
                    isDone = true
                    try {
                        lock.notifyAll()
                    } catch (ignored: Exception) {

                    }

                }
            }
        }
    }

    /**
     * Wait to run end
     *
     * @return T
     */
    fun waitRun(): T? {
        if (!isDone) {
            synchronized(lock) {
                while (!isDone) {
                    try {
                        lock.wait()
                    } catch (ignored: InterruptedException) {
                    }

                }
            }
        }
        return mResult
    }

    /**
     * Wait for a period of time to run end
     *
     * @param waitMillis      wait milliseconds time
     * @param waitNanos       wait nanoseconds time
     * @param cancelOnTimeOut True if when wait end cancel the runner
     * @return T
     */
    fun waitRun(waitMillis: Long, waitNanos: Int, cancelOnTimeOut: Boolean): T? {
        if (!isDone) {
            synchronized(lock) {
                if (!isDone) {
                    try {
                        lock.wait(waitMillis, waitNanos)
                    } catch (ignored: InterruptedException) {
                    } finally {
                        if (!isDone && cancelOnTimeOut)
                            isDone = true
                    }
                }
            }
        }
        return mResult
    }

    override fun setPool(pool: Queue<Task>) {
        mPool = pool
    }

    @Synchronized
    override fun cancel() {
        if (!isDone) {
            isDone = true
            // clear the task form pool
            if (mPool != null) {

                if (mPool != null) {
                    try {
                        mPool!!.remove(this)
                    } catch (e: Exception) {
                        e.stackTrace
                    } finally {
                        mPool = null
                    }
                }
            }
        }
    }
}
