package com.github.mtdhllf.kit.coroutines

/**
 * author: mtdhllf
 * time  : 2019/06/17 11:13
 * desc  : Run callback, the callback can support return any value
 */
interface Func<Out> {

    fun call(): Out

}