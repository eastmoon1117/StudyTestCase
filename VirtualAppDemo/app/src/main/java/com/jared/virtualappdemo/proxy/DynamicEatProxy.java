package com.jared.virtualappdemo.proxy;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by jared on 2018/2/24.
 */

public class DynamicEatProxy implements InvocationHandler {
    private Object object;

    public DynamicEatProxy(Object T) {
        object = T;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        wash();
        Object returnValue = method.invoke(object, args);
        return returnValue;
    }

    void wash() {
        Log.d("DynamicEatProxy", "Wash Hands");
    }
}
