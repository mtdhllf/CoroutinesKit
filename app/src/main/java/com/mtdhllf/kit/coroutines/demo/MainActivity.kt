package com.mtdhllf.kit.coroutines.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.mtdhllf.kit.coroutines.Action
import com.github.mtdhllf.kit.coroutines.Func
import com.github.mtdhllf.kit.coroutines.Run

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i("test1", "start")
        Run.onUiSync(object : Action {
            override fun call() {
                Log.i("test1", "finish")
            }
        })

        Log.i("test2", "start")
        val test2 = Run.onUiSync(object :
            Func<Int> {
            override fun call(): Int {
                return 1
            }
        })
        if (test2 == 1) {
            Log.i("test2", "finish")
        }

        Log.i("test3", "start")
        Run.onUiSyncDelay(object : Action {
            override fun call() {
                Log.i("test3", "finish")
            }
        }, 2000)

        Log.i("test4", "start")
        Run.onUiSyncAtTime(object : Action {
            override fun call() {
                Log.i("test4", "finish")
            }
        },System.currentTimeMillis()+1000L)

        Log.i("test5", "start")
        Run.onUiASync(object : Action {
            override fun call() {
                Log.i("test5", "finish")
            }
        })

        Log.i("test6", "start")
        Run.onUiASync(object : Action {
            override fun call() {
                Log.i("test6", "finish")
            }
        },2000)

        Log.i("test7", "start")
        Run.onUiASyncDelay(object : Action {
            override fun call() {
                Log.i("test7", "finish")
            }
        }, 2000)

        Log.i("test8", "start")
        Run.onUiASyncAtTime(object : Action {
            override fun call() {
                Log.i("test8", "finish")
            }
        },System.currentTimeMillis()+1500)

        Log.i("test9", "start")
        Run.onBackground(object : Action {
            override fun call() {
                Log.i("test9", "finish")
            }
        })

        Log.i("test10", "start")
        Run.onBackground(object : Action {
            override fun call() {
                Log.i("test10", "finish")
            }
        },100L)

        Log.i("test11", "start")
        Run.onBackgroundDelay(object : Action {
            override fun call() {
                Log.i("test11", "finish")
            }
        },500L)

        Log.i("test12", "start")
        Run.onBackgroundAtTime(object : Action {
            override fun call() {
                Log.i("test12", "finish")
            }

        },System.currentTimeMillis()+1000L)

    }

}
