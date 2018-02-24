package com.jared.virtualappdemo.proxy;

import android.util.Log;

/**
 * Created by jared on 2018/2/24.
 */

public class StaticEatProxy implements Human {
    @Override
    public void eat(String something) {
        HumanImpl human = new HumanImpl();
        wash();
        human.eat(something);
        wipeMouth();
    }

    @Override
    public void toilet() {
        HumanImpl human = new HumanImpl();
        wash();
        human.toilet();
        wash();
    }

    void wash() {
        Log.d("StaticEatProxy", "Wash Hands");
    }

    void wipeMouth() {
        Log.d("StaticEatProxy", "Wipe Mouth");
    }
}
