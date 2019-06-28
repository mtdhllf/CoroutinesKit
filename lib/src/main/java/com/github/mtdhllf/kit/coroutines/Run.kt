package com.github.mtdhllf.kit.coroutines

import android.os.Looper
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.*

/**
 * author: mtdhllf
 * This is UI operation class
 * You can run backgroundThread or MainThread By Async and Sync
 */

object Run {

    val uiPoster = HandlerPoster(Looper.getMainLooper(), 16, false)

    /**
     * UI线程同步执行
     * @param action 任务
     */
    @JvmStatic
    fun onUiSync(action: Action) {
        uiPoster.post { action.call() }
    }

    /**
     * UI线程同步执行
     * @param action 任务
     */
    fun onUiSync(action: () -> Unit) {
        uiPoster.post { action() }
    }

    /**
     * UI线程同步执行
     * @param func 带结果任务
     * @return [T]
     */
    @JvmStatic
    fun <T> onUiSync(func: Func<T>): T? {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return func.call()
        }
        val poster = FuncSyncTask(func)
        uiPoster.sync(poster)
        return poster.waitRun()
    }

    /**
     * UI线程延迟同步执行
     * @param action 任务
     * @param delay  延迟时间
     */
    @JvmStatic
    fun onUiSyncDelay(action: Action, delay: Long) {
        uiPoster.postDelayed({ action.call() }, delay)
    }

    /**
     * UI线程延迟同步执行
     * @param action 任务
     * @param delay  延迟时间
     */
    fun onUiSyncDelay(action: () -> Unit, delay: Long) {
        uiPoster.postDelayed({ action() }, delay)
    }

    /**
     * UI线程定时同步执行
     * @param action 任务
     * @param atTime 定时时间(需要大于当前时间,时间差太远建议使用 [Run.onUiASyncAtTime] )
     */
    @JvmStatic
    fun onUiSyncAtTime(action: Action, atTime: Long) {
        uiPoster.postAtTime({ action.call() }, atTime - System.currentTimeMillis() + SystemClock.uptimeMillis())
    }

    /**
     * UI线程定时同步执行
     * @param action 任务
     * @param atTime 定时时间(需要大于当前时间,时间差太远建议使用 [Run.onUiASyncAtTime] )
     */
    fun onUiSyncAtTime(action: () -> Unit, atTime: Long) {
        uiPoster.postAtTime({ action() }, atTime - System.currentTimeMillis() + SystemClock.uptimeMillis())
    }

    /**
     * UI线程异步执行
     * @param action 任务
     * @return 可主动取消的任务
     */
    @JvmStatic
    fun onUiASync(action: Action): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            action.call()
        }
    }

    /**
     * UI线程异步执行
     * @param action 任务
     * @return 可主动取消的任务
     */
    fun onUiASync(action: () -> Unit): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            action()
        }
    }

    /**
     * UI线程异步执行(超时检查)
     * @param  action            任务
     * @param  millisTimeOut 超时时间
     * @return 可主动取消的任务
     */
    @JvmStatic
    @Deprecated(level = DeprecationLevel.WARNING, message = "测试不够充分,请谨慎使用", replaceWith = ReplaceWith(""))
    fun onUiASync(action: Action, millisTimeOut: Long): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            withTimeoutOrNull(millisTimeOut) {
                action.call()
            }
        }
    }

    /**
     * UI线程异步执行(超时检查)
     * @param  action            任务
     * @param  millisTimeOut 超时时间
     * @return 可主动取消的任务
     */
    @Deprecated(level = DeprecationLevel.WARNING, message = "测试不够充分,请谨慎使用", replaceWith = ReplaceWith(""))
    fun onUiASync(action: () -> Unit, millisTimeOut: Long): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            withTimeoutOrNull(millisTimeOut) {
                action()
            }
        }
    }

    /**
     * UI线程异步延迟执行
     * @param action 任务
     * @param delay  延迟时间
     * @return 可主动取消的任务
     */
    @JvmStatic
    fun onUiASyncDelay(action: Action, delay: Long): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            delay(delay)
            action.call()
        }
    }

    /**
     * UI线程异步延迟执行
     * @param action 任务
     * @param delay  延迟时间
     * @return 可主动取消的任务
     */
    fun onUiASyncDelay(action: () -> Unit, delay: Long): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            delay(delay)
            action()
        }
    }

    /**
     * UI线程异步延迟执行
     * @param  action 任务
     * @param  atTime 指定时间(需要大于当前时间,否则立即执行)
     * @return 可主动取消的任务
     */
    @JvmStatic
    fun onUiASyncAtTime(action: Action, atTime: Long): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            delay(atTime - System.currentTimeMillis())
            action.call()
        }
    }

    /**
     * UI线程异步延迟执行
     * @param  action 任务
     * @param  atTime 指定时间(需要大于当前时间,否则立即执行)
     * @return 可主动取消的任务
     */
    fun onUiASyncAtTime(action: () -> Unit, atTime: Long): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            delay(atTime - System.currentTimeMillis())
            action()
        }
    }

    /**
     * IO线程异步执行
     * @param action 任务
     * @return 可主动取消的任务
     */
    @JvmStatic
    fun onBackground(action: Action): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            action.call()
        }
    }

    /**
     * IO线程异步执行
     * @param action 任务
     * @return 可主动取消的任务
     */
    fun onBackground(action: () -> Unit): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            action()
        }
    }

    /**
     * IO线程异步执行(超时检查)
     * @param  action            任务
     * @param  millisTimeOut 超时时间
     * @return 可主动取消的任务
     */
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "测试不够充分,请谨慎使用", replaceWith = ReplaceWith(""))
    fun onBackground(action: Action, millisTimeOut: Long): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            withTimeoutOrNull(millisTimeOut) {
                action.call()
            }
        }
    }

    /**
     * IO线程异步执行(超时检查)
     * @param  action            任务
     * @param  millisTimeOut 超时时间
     * @return 可主动取消的任务
     */
    @Deprecated(level = DeprecationLevel.WARNING, message = "测试不够充分,请谨慎使用", replaceWith = ReplaceWith(""))
    fun onBackground(action: () -> Unit, millisTimeOut: Long): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            withTimeoutOrNull(millisTimeOut) {
                action()
            }
        }
    }

    /**
     * IO线程异步延迟执行
     * @param action 任务
     * @param delay  延迟时间
     * @return 可主动取消的任务
     */
    @JvmStatic
    fun onBackgroundDelay(action: Action, delay: Long): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            delay(delay)
            action.call()
        }
    }

    /**
     * IO线程异步延迟执行
     * @param action 任务
     * @param delay  延迟时间
     * @return 可主动取消的任务
     */
    fun onBackgroundDelay(action: () -> Unit, delay: Long): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            delay(delay)
            action()
        }
    }

    /**
     * IO线程异步延迟执行
     * @param  action 任务
     * @param  atTime 指定时间(需要大于当前时间,否则立即执行)
     * @return 可主动取消的任务
     */
    @JvmStatic
    fun onBackgroundAtTime(action: Action, atTime: Long): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            delay(atTime - System.currentTimeMillis())
            action.call()
        }
    }

    /**
     * IO线程异步延迟执行
     * @param  action 任务
     * @param  atTime 指定时间(需要大于当前时间,否则立即执行)
     * @return 可主动取消的任务
     */
    fun onBackgroundAtTime(action: () -> Unit, atTime: Long): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            delay(atTime - System.currentTimeMillis())
            action()
        }
    }


    /**
     * 创建UI异步心跳任务
     * @param interval 心跳回调,你可以传入[AbsInterval]按需覆写你需要的接口
     * @param times    心跳次数
     * @param period   心跳间隔(毫秒)
     * @return 可取消的任务
     */
    @JvmStatic
    fun onUiTimesInterval(interval: Interval, times: Int, period: Long): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            var done = false
            try {
                repeat(times) { i ->
                    interval.tick(i)
                    done = i == times - 1
                    delay(period)
                }

            } finally {
                if (done) {
                    interval.finish()
                } else {
                    interval.cancel()
                }
            }

        }
    }

    /**
     * 创建UI异步心跳任务
     * @param interval 心跳任务
     * @param times    心跳次数
     * @param period   心跳间隔(毫秒)
     * @param delay    延迟执行(毫秒)
     * @return 可取消的任务
     */
    fun onUiTimesInterval(interval: (index: Int) -> Unit, times: Int, period: Long, delay: Long = 0): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            delay(delay)
            repeat(times) { i ->
                interval(i)
                delay(period)
            }
        }
    }

    /**
     * 创建UI异步心跳任务
     * @param interval 心跳回调,你可以传入[AbsInterval]按需覆写你需要的接口
     * @param times    心跳次数
     * @param period   心跳间隔(毫秒)
     * @param delay    延迟执行(毫秒)
     * @return 可取消的任务
     */
    @JvmStatic
    fun onUiTimesInterval(interval: Interval, times: Int, period: Long, delay: Long = 0): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            var done = false
            try {
                delay(delay)
                repeat(times) { i ->
                    interval.tick(i)
                    done = i == times - 1
                    delay(period)
                }

            } finally {
                if (done) {
                    interval.finish()
                } else {
                    interval.cancel()
                }
            }
        }
    }


    /**
     * 创建UI异步心跳任务
     * @param interval 心跳任务
     * @param times    心跳次数
     * @param period   心跳间隔(毫秒)
     * @param delay    延迟执行(毫秒)
     * @return 可取消的任务
     */
    fun onUiTimesInterval(
        interval: (index: Int) -> Unit,
        finish: () -> Unit,
        cancel: () -> Unit,
        times: Int,
        period: Long,
        delay: Long = 0
    ): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            var done = false
            try {
                delay(delay)
                repeat(times) { i ->
                    interval(i)
                    done = i == times - 1
                    delay(period)
                }

            } finally {
                if (done) {
                    finish()
                } else {
                    cancel()
                }
            }

        }
    }


    /**
     * 创建异步心跳任务
     * @param interval 心跳回调,你可以传入[AbsInterval]按需覆写你需要的接口
     * @param times    心跳次数
     * @param period   心跳间隔(毫秒)
     * @return 可取消的任务
     */
    @JvmStatic
    fun onIOTimesInterval(interval: Interval, times: Int, period: Long): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            var done = false
            try {
                repeat(times) { i ->
                    interval.tick(i)
                    done = i == times - 1
                    delay(period)
                }

            } finally {
                if (done) {
                    interval.finish()
                } else {
                    interval.cancel()
                }
            }

        }
    }

    /**
     * 创建IO心跳任务
     * @param [interval] 心跳任务
     * @param [count]    心跳次数
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     */
    fun onIOTimesInterval(interval: (index: Int) -> Unit, count: Int, period: Long, delay: Long = 0): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            delay(delay)
            repeat(count) { i ->
                interval(i)
                delay(period)
            }
        }
    }

    /**
     * 创建异步心跳任务
     * @param [interval] 心跳回调,你可以传入[AbsInterval]按需覆写你需要的接口
     * @param [times]    心跳次数
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     * @return 可取消的任务
     */
    @JvmStatic
    fun onIOTimesInterval(interval: Interval, times: Int, period: Long, delay: Long = 0): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            var done = false
            try {
                delay(delay)
                repeat(times) { i ->
                    interval.tick(i)
                    done = i == times - 1
                    delay(period)
                }

            } finally {
                if (done) {
                    interval.finish()
                } else {
                    interval.cancel()
                }
            }
        }
    }

    /**
     * 创建异步心跳任务
     * @param [interval] 心跳任务
     * @param [finish]   完成任务
     * @param [cancel]   取消任务
     * @param [times]    心跳次数
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     * @return 可取消的任务
     */
    fun onIOTimesInterval(
        interval: (index: Int) -> Unit,
        finish: () -> Unit,
        cancel: () -> Unit,
        times: Int,
        period: Long,
        delay: Long = 0
    ): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            var done = false
            try {
                delay(delay)
                repeat(times) { i ->
                    interval(i)
                    done = i == times - 1
                    delay(period)
                }

            } finally {
                if (done) {
                    finish()
                } else {
                    cancel()
                }
            }

        }
    }

}