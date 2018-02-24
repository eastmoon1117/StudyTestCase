package com.jared.virtualappdemo.proxy;

import java.lang.reflect.Proxy;

/**
 * Created by jared on 2018/2/24.
 */

public class TestProxy {
    public static void NoProxy() {
        Human human = new HumanImpl();
        human.eat("rice");
    }

    public static void StaticProxy() {
        Human human = new StaticEatProxy();
        human.eat("rice");
    }

    public static void DynamicProxy() {
        HumanImpl human = new HumanImpl();
        DynamicEatProxy action = new DynamicEatProxy(human);
        ClassLoader loader = human.getClass().getClassLoader();
        Class[] interfaces = human.getClass().getInterfaces();
        Object proxy = Proxy.newProxyInstance(loader, interfaces, action);
        Human humanProxy = (Human)proxy;
        humanProxy.eat("rice");
        humanProxy.toilet();
    }
}
