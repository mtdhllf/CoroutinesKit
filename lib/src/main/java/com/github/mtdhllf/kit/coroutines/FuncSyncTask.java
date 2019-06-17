package com.github.mtdhllf.kit.coroutines;

import java.util.Queue;
import java.lang.Runnable;

/**
 * FuncSyncTask use to {@link Func} and {@link Runnable}
 * See {@link Run} call this
 */
@SuppressWarnings("unused")
final class FuncSyncTask<T> implements Func<T>, Task {

    private final Func<T> mFunc;
    private T mResult;
    private boolean mDone = false;
    private Queue<Task> mPool = null;

    /**
     * In this we call cal the {@link Func}
     * and check should run it
     *
     * @return T
     */
    FuncSyncTask(Func<T> func) {
        this.mFunc = func;
    }

    @Override
    public T call() {
        // Cleanup reference the pool
        mPool = null;
        // Doing
        return mFunc.call();
    }

    /**
     * Run to doing something
     */
    @Override
    public void run() {
        if (!mDone) {
            synchronized (this) {
                if (!mDone) {
                    mResult = call();
                    mDone = true;
                    try {
                        this.notifyAll();
                    } catch (Exception ignored) {
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
    T waitRun() {
        if (!mDone) {
            synchronized (this) {
                while (!mDone) {
                    try {
                        this.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
        return mResult;
    }

    /**
     * Wait for a period of time to run end
     *
     * @param waitMillis      wait milliseconds time
     * @param waitNanos       wait nanoseconds time
     * @param cancelOnTimeOut True if when wait end cancel the runner
     * @return T
     */
    T waitRun(long waitMillis, int waitNanos, boolean cancelOnTimeOut) {
        if (!mDone) {
            synchronized (this) {
                if (!mDone) {
                    try {
                        this.wait(waitMillis, waitNanos);
                    } catch (InterruptedException ignored) {
                    } finally {
                        if (!mDone && cancelOnTimeOut)
                            mDone = true;
                    }
                }
            }
        }
        return mResult;
    }

    @Override
    public void setPool(Queue<Task> pool) {
        mPool = pool;
    }

    @Override
    public boolean isDone() {
        return mDone;
    }

    @Override
    public void cancel() {
        if (!mDone) {
            synchronized (this) {
                mDone = true;

                // clear the task form pool
                if (mPool != null) {
                    //noinspection SynchronizeOnNonFinalField
                    synchronized (mPool) {
                        if (mPool != null) {
                            try {
                                mPool.remove(this);
                            } catch (Exception e) {
                                e.getStackTrace();
                            } finally {
                                mPool = null;
                            }
                        }
                    }
                }
            }
        }
    }
}
