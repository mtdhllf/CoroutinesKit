package com.github.mtdhllf.kit.coroutines

/**
 * author: mtdhllf
 * time  : 2019/06/28 9:54
 * desc  :
 */
interface Interval {

    /**
     * @param index in [0,times) or (0 until times)
     */
    fun tick(index:Int)

    /**
     * Job finish
     */
    fun finish()

    /**
     * on Job invoke like "cancel" methods
     */
    fun cancel()

}
