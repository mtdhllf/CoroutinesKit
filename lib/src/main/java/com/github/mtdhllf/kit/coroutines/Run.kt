package com.github.mtdhllf.kit.coroutines

import android.os.Looper
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.*
import java.lang.Runnable

/**
 * author: mtdhllf
 * This is UI operation class
 * You can run backgroundThread or MainThread By Async and Sync
 */

object Run {

    val uiPoster = HandlerPoster(Looper.getMainLooper(), 16, false)

    /**
     * UI线程同步执行
     * @param [action] 任务
     */
    @JvmStatic
    fun onUiSync(action: Action) {
        uiPoster.post { action.call() }
    }

    /**
     * UI线程同步执行
     * @param [action] 任务
     */
    fun onUiSync(action: () -> Unit) {
        uiPoster.post { action() }
    }

    /**
     * UI线程同步执行
     * @param [func] 带结果任务
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
     * @param [delay]  延迟时间
     * @param [action] 任务
     */
    @JvmStatic
    fun onUiSyncDelay(delay: Long, action: Action) {
        uiPoster.postDelayed({ action.call() }, delay)
    }

    /**
     * UI线程延迟同步执行
     * @param [delay]  延迟时间
     * @param [action] 任务
     */
    fun onUiSyncDelay(delay: Long, action: () -> Unit) {
        uiPoster.postDelayed({ action() }, delay)
    }


    /**
     * UI线程定时同步执行
     * @param [atTime] 定时时间(需要大于当前时间,时间差太远建议使用 [Run.onUiASyncAtTime] )
     * @param [action] 任务
     */
    @JvmStatic
    fun onUiSyncAtTime(atTime: Long, action: Action) {
        uiPoster.postAtTime(
            { action.call() },
            atTime - System.currentTimeMillis() + SystemClock.uptimeMillis()
        )
    }

    /**
     * UI线程定时同步执行
     * @param [atTime] 定时时间(需要大于当前时间,时间差太远建议使用 [Run.onUiASyncAtTime] )
     * @param [action] 任务
     */
    fun onUiSyncAtTime(atTime: Long, action: () -> Unit) {
        uiPoster.postAtTime(
            { action() },
            atTime - System.currentTimeMillis() + SystemClock.uptimeMillis()
        )
    }

    /**
     * UI线程异步执行
     * @param [action] 任务
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
     * @param [action] 任务
     * @return 可主动取消的任务
     */
    fun onUiASync(action: () -> Unit): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            action()
        }
    }

    /**
     * UI线程异步执行(超时检查)
     * @param [millisTimeOut] 超时时间
     * @param [action]            任务
     * @return 可主动取消的任务
     */
    @JvmStatic
    @Deprecated(
        level = DeprecationLevel.WARNING,
        message = "测试未通过,请谨慎使用",
        replaceWith = ReplaceWith("")
    )
    fun onUiASync(millisTimeOut: Long, action: Action): Job {
        val job = GlobalScope.launch(Dispatchers.Main) {
            action.call()
        }
        uiPoster.postDelayed({ job.cancel() }, millisTimeOut)
        return job
    }

    /**
     * UI线程异步执行(超时检查)
     * @param [millisTimeOut] 超时时间
     * @param [action]            任务
     * @return 可主动取消的任务
     */
    @Deprecated(
        level = DeprecationLevel.WARNING,
        message = "测试未通过,请谨慎使用",
        replaceWith = ReplaceWith("")
    )
    fun onUiASync(millisTimeOut: Long, action: () -> Unit): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            withTimeoutOrNull(millisTimeOut) {
                action()
            }
        }
    }


    /**
     * UI线程异步延迟执行
     * @param [delay]  延迟时间
     * @param [action] 任务
     * @return 可主动取消的任务
     */
    @JvmStatic
    fun onUiASyncDelay(delay: Long, action: Action): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            delay(delay)
            action.call()
        }
    }

    /**
     * UI线程异步延迟执行
     * @param [delay]  延迟时间
     * @param [action] 任务
     * @return 可主动取消的任务
     */
    fun onUiASyncDelay(delay: Long, action: () -> Unit): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            delay(delay)
            action()
        }
    }


    /**
     * UI线程异步延迟执行
     * @param [atTime] 指定时间(需要大于当前时间,否则立即执行)
     * @param [action] 任务
     * @return 可主动取消的任务
     */
    @JvmStatic
    fun onUiASyncAtTime(atTime: Long, action: Action): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            delay(atTime - System.currentTimeMillis())
            action.call()
        }
    }

    /**
     * UI线程异步延迟执行
     * @param [atTime] 指定时间(需要大于当前时间,否则立即执行)
     * @param [action] 任务
     * @return 可主动取消的任务
     */
    fun onUiASyncAtTime(atTime: Long, action: () -> Unit): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            delay(atTime - System.currentTimeMillis())
            action()
        }
    }


    /**
     * IO线程异步执行
     * @param [action] 任务
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
     * @param [action] 任务
     * @return 可主动取消的任务
     */
    fun onBackground(action: () -> Unit): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            action()
        }
    }

    /**
     * IO线程异步执行(超时检查)
     * @param [millisTimeOut]     超时时间
     * @param [action]            任务
     * @return 可主动取消的任务
     */
    @Deprecated(
        level = DeprecationLevel.HIDDEN,
        message = "测试未通过,请谨慎使用",
        replaceWith = ReplaceWith("")
    )
    fun onBackground(millisTimeOut: Long, action: Action): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            withTimeoutOrNull(millisTimeOut) {
                action.call()
            }
        }
    }

    /**
     * IO线程异步执行(超时检查)
     * @param [millisTimeOut] 超时时间
     * @param [action]        任务
     * @return 可主动取消的任务
     */
    @Deprecated(
        level = DeprecationLevel.WARNING,
        message = "测试未通过,请谨慎使用",
        replaceWith = ReplaceWith("")
    )
    fun onBackground(millisTimeOut: Long, action: () -> Unit): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            withTimeoutOrNull(millisTimeOut) {
                action()
            }
        }
    }


    /**
     * IO线程异步延迟执行
     * @param [delay]  延迟时间
     * @param [action] 任务
     * @return 可主动取消的任务
     */
    @JvmStatic
    fun onBackgroundDelay(delay: Long, action: Action): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            delay(delay)
            action.call()
        }
    }

    /**
     * IO线程异步延迟执行
     * @param [delay]  延迟时间
     * @param [action] 任务
     * @return 可主动取消的任务
     */
    fun onBackgroundDelay(delay: Long, action: () -> Unit): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            delay(delay)
            action()
        }
    }


    /**
     * IO线程异步延迟执行
     * @param [atTime] 指定时间(需要大于当前时间,否则立即执行)
     * @param [action] 任务
     * @return 可主动取消的任务
     */
    @JvmStatic
    fun onBackgroundAtTime(atTime: Long, action: Action): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            delay(atTime - System.currentTimeMillis())
            action.call()
        }
    }

    /**
     * IO线程异步延迟执行
     * @param  [atTime] 指定时间(需要大于当前时间,否则立即执行)
     * @param  [action] 任务
     * @return 可主动取消的任务
     */
    fun onBackgroundAtTime(atTime: Long, action: () -> Unit): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            delay(atTime - System.currentTimeMillis())
            action()
        }
    }

    /**
     * 创建UI异步心跳任务,注意:此方法不会回调[Interval.finish]接口
     * @param [period]   心跳间隔(毫秒)
     * @param [interval] 心跳回调,你可以传入[AbsInterval]按需覆写你需要的接口
     * @return 可取消的任务
     */
    @JvmStatic
    fun onUiInterval(period: Long, interval: Interval): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            var index = 0
            try {
                while (isActive) {
                    interval.tick(index++)
                    delay(period)
                }
            } finally {
                interval.cancel()
            }
        }
    }

    /**
     * 创建UI异步心跳任务,注意:此方法不会回调[Interval.finish]接口
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     * @param [interval] 心跳回调,你可以传入[AbsInterval]按需覆写你需要的接口
     * @return 可取消的任务
     */
    @JvmStatic
    fun onUiInterval(period: Long, delay: Long, interval: Interval): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            var index = 0
            try {
                delay(delay)
                while (isActive) {
                    interval.tick(index++)
                    delay(period)
                }

            } finally {
                interval.cancel()
            }

        }
    }

    /**
     * 创建UI异步心跳任务
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     * @param [interval] 心跳任务
     * @return 可取消的任务
     */
    fun onUiInterval(period: Long, delay: Long = 0, interval: (index: Int) -> Unit): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            var index = 0
            delay(delay)
            while (isActive) {
                interval(index++)
                delay(period)
            }
        }
    }

    /**
     * 创建UI异步心跳任务
     * @param [cancel]   取消任务
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     * @param [interval] 心跳任务
     * @return 可取消的任务
     */
    fun onUiInterval(
        cancel: () -> Unit,
        period: Long,
        delay: Long = 0,
        interval: (index: Int) -> Unit
    ): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            var index = 0
            try {
                delay(delay)
                while (isActive) {
                    interval(index++)
                    delay(period)
                }
            } finally {
                cancel()
            }
        }
    }

    /**
     * 创建异步心跳任务,注意:此方法不会回调[Interval.finish]接口
     * @param [period]   心跳间隔(毫秒)
     * @param [interval] 心跳回调,你可以传入[AbsInterval]按需覆写你需要的接口
     * @return 可取消的任务
     */
    @JvmStatic
    fun onIOInterval(period: Long, interval: Interval): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            var index = 0
            try {
                while (isActive) {
                    interval.tick(index++)
                    delay(period)
                }
            } finally {
                interval.cancel()
            }
        }
    }

    /**
     * 创建异步心跳任务,注意:此方法不会回调[Interval.finish]接口
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     * @param [interval] 心跳回调,你可以传入[AbsInterval]按需覆写你需要的接口
     * @return 可取消的任务
     */
    @JvmStatic
    fun onIOInterval(period: Long, delay: Long,interval: Interval): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            var index = 0
            try {
                delay(delay)
                while (isActive) {
                    interval.tick(index++)
                    delay(period)
                }

            } finally {
                interval.cancel()
            }

        }
    }

    /**
     * 创建异步心跳任务
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     * @param [interval] 心跳任务
     * @return 可取消的任务
     */
    fun onIOInterval(period: Long, delay: Long = 0,interval: (index: Int) -> Unit): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            var index = 0
            delay(delay)
            while (isActive) {
                interval(index++)
                delay(period)
            }
        }
    }

    /**
     * 创建异步心跳任务
     * @param [cancel]   取消任务
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     * @param [interval] 心跳任务
     * @return 可取消的任务
     */
    fun onIOInterval(
        cancel: () -> Unit,
        period: Long,
        delay: Long = 0,
        interval: (index: Int) -> Unit
    ): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            var index = 0
            try {
                delay(delay)
                while (isActive) {
                    interval(index++)
                    delay(period)
                }
            } finally {
                cancel()
            }
        }
    }


    /**
     * 创建UI异步心跳任务
     * @param [times]    心跳次数
     * @param [period]   心跳间隔(毫秒)
     * @param [interval] 心跳回调,你可以传入[AbsInterval]按需覆写你需要的接口
     * @return 可取消的任务
     */
    @JvmStatic
    fun onUiTimesInterval(times: Int, period: Long,interval: Interval): Job {
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
     * @param [times]    心跳次数
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     * @param [interval] 心跳任务
     * @return 可取消的任务
     */
    fun onUiTimesInterval(
        times: Int,
        period: Long,
        delay: Long = 0,
        interval: (index: Int) -> Unit
    ): Job {
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
     * @param [times]    心跳次数
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     * @param [interval] 心跳回调,你可以传入[AbsInterval]按需覆写你需要的接口
     * @return 可取消的任务
     */
    @JvmStatic
    fun onUiTimesInterval(times: Int, period: Long, delay: Long = 0,interval: Interval): Job {
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
     * 创建异步心跳任务
     * @param [times]    心跳次数
     * @param [period]   心跳间隔(毫秒)
     * @param [interval] 心跳回调,你可以传入[AbsInterval]按需覆写你需要的接口
     * @return 可取消的任务
     */
    @JvmStatic
    fun onIOTimesInterval(times: Int, period: Long,interval: Interval): Job {
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
     * @param [count]    心跳次数
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     * @param [interval] 心跳任务
     */
    fun onIOTimesInterval(
        count: Int,
        period: Long,
        delay: Long = 0,
        interval: (index: Int) -> Unit
    ): Job {
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
     * @param [times]    心跳次数
     * @param [period]   心跳间隔(毫秒)
     * @param [delay]    延迟执行(毫秒)
     * @param [interval] 心跳回调,你可以传入[AbsInterval]按需覆写你需要的接口
     * @return 可取消的任务
     */
    @JvmStatic
    fun onIOTimesInterval(times: Int, period: Long, delay: Long = 0, interval: Interval): Job {
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

}