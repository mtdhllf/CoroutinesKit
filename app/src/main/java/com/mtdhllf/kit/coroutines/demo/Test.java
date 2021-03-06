package com.mtdhllf.kit.coroutines.demo;

import android.util.Log;
import com.github.mtdhllf.kit.coroutines.AbsInterval;
import com.github.mtdhllf.kit.coroutines.Interval;
import com.github.mtdhllf.kit.coroutines.Run;
import kotlinx.coroutines.Job;

/**
 * author: mtdhllf
 * time  : 2019/06/28 11:42
 * desc  :
 */
public class Test {

    public static Job test13(){

        Log.i("test13","start");
        return Run.onUiTimesInterval(10,1000,new AbsInterval() {
            @Override
            public void tick(int index) {
                Log.i("test13",index+"");
            }

            @Override
            public void finish() {
                Log.i("test13","finish");
            }

            @Override
            public void cancel() {
                Log.i("test13","cancel");
            }
        });

    }

    public static Job test14(){

        Log.i("test14","start");
        return Run.onUiInterval(1000,new Interval(){

            @Override
            public void cancel() {
                Log.i("test14", "cancel");
            }

            @Override
            public void finish() {
                Log.i("test14", "finish");
            }

            @Override
            public void tick(int index) {
                Log.i("test14", index+"");
            }

        });
    }

}
