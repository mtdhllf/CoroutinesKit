package com.github.mtdhllf.kit.coroutines

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import com.github.mtdhllf.kit.coroutines.Poster.Companion.ASYNC
import com.github.mtdhllf.kit.coroutines.Poster.Companion.SYNC

import java.util.LinkedList
import java.util.NoSuchElementException
import java.util.Queue

/**
 * Run Handler Poster extends Handler
 * Init this
 * In class have two Dispatcher with [mSyncDispatcher] [mAsyncDispatcher]
 * @param looper                       Handler Looper
 * @param maxMillisInsideHandleMessage The maximum time occupied the main thread each cycle
 * @param onlyAsync                    If TRUE the [mSyncDispatcher] same as [mAsyncDispatcher]
 */
class HandlerPoster(looper: Looper, maxMillisInsideHandleMessage: Int, onlyAsync: Boolean) : Handler(looper),
    Poster {
    private val mAsyncDispatcher: Dispatcher
    private val mSyncDispatcher: Dispatcher

    init {
        // inside time
        MAX_MILLIS_INSIDE_HANDLE_MESSAGE = maxMillisInsideHandleMessage

        // async runner
        mAsyncDispatcher = Dispatcher(LinkedList(),
            object : Dispatcher.IPoster {
                override fun sendMessage() {
                    this@HandlerPoster.sendMessage(ASYNC)
                }
            })

        // sync runner
        if (onlyAsync) {
            mSyncDispatcher = mAsyncDispatcher
        } else {
            mSyncDispatcher = Dispatcher(LinkedList(),
                object : Dispatcher.IPoster {
                    override fun sendMessage() {
                        this@HandlerPoster.sendMessage(SYNC)
                    }
                })
        }
    }

    /**
     * Pool clear
     */
    override fun dispose() {
        this.removeCallbacksAndMessages(null)
        this.mAsyncDispatcher.dispose()
        this.mSyncDispatcher.dispose()
    }

    /**
     * Add a async post to Handler pool
     *
     * @param task [Task]
     */
    override fun async(task: Task) {
        mAsyncDispatcher.offer(task)
    }

    /**
     * Add a async post to Handler pool
     *
     * @param task [Task]
     */
    override fun sync(task: Task) {
        mSyncDispatcher.offer(task)
    }

    /**
     * Run in main thread
     *
     * @param msg call messages
     */
    override fun handleMessage(msg: Message) {
        if (msg.what == ASYNC) {
            mAsyncDispatcher.dispatch()
        } else if (msg.what == SYNC) {
            mSyncDispatcher.dispatch()
        } else
            super.handleMessage(msg)
    }

    /**
     * Send a message to this Handler
     *
     * @param what This what is SYNC or ASYNC
     */
    private fun sendMessage(what: Int) {
        if (!sendMessage(obtainMessage(what))) {
            throw RuntimeException("Could not send handler message")
        }
    }


    /**
     * This's main Dispatcher
     */
    private class Dispatcher internal constructor(private val mPool: Queue<Task>, private var mPoster: IPoster?) {
        private var isActive: Boolean = false

        /**
         * offer to [mPool]
         *
         * @param task [Task]
         */
        fun offer(task: Task) {
            synchronized(mPool) {
                // offer to queue pool
                mPool.offer(task)
                // set the task pool reference
                task.setPool(mPool)

                if (!isActive) {
                    isActive = true
                    // send again message
                    val poster = mPoster
                    poster?.sendMessage()
                }
            }
        }

        /**
         * dispatch form [mPool]
         */
        fun dispatch() {
            var rescheduled = false
            try {
                val started = SystemClock.uptimeMillis()
                while (true) {
                    var runnable = poll()
                    if (runnable == null) {
                        synchronized(mPool) {
                            // Check again, this time in synchronized
                            runnable = poll()
                            if (runnable == null) {
                                isActive = false
                                return
                            }
                        }
                    }
                    runnable!!.run()
                    val timeInMethod = SystemClock.uptimeMillis() - started
                    if (timeInMethod >= MAX_MILLIS_INSIDE_HANDLE_MESSAGE) {
                        // send again message
                        val poster = mPoster
                        poster?.sendMessage()

                        // rescheduled is true
                        rescheduled = true
                        return
                    }
                }
            } finally {
                isActive = rescheduled
            }
        }

        /**
         * dispose the Dispatcher on your no't need use
         */
        fun dispose() {
            mPool.clear()
            mPoster = null
        }

        /**
         * poll a Runnable form [mPool]
         *
         * @return Runnable
         */
        private fun poll(): Runnable? {
            synchronized(mPool) {
                try {
                    return mPool.poll()
                } catch (e: NoSuchElementException) {
                    e.printStackTrace()
                    return null
                }

            }
        }

        /**
         * This's poster can to send refresh message
         */
        interface IPoster {
            fun sendMessage()
        }
    }

    companion object {
        private var MAX_MILLIS_INSIDE_HANDLE_MESSAGE = 16
    }
}
