package com.jared.virtualappdemo.proxy;

import android.util.Log;

/**
 * Created by jared on 2018/2/24.
 */

public class HumanImpl implements Human {
    @Override
    public void eat(String something) {
        Log.d("HumanImpl", "Eat "+something);
    }

    @Override
    public void toilet() {
        Log.d("HumanImpl", "toilet");
    }
}
