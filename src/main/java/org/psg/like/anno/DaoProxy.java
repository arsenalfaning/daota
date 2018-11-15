package org.psg.like.anno;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DaoProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = method.getReturnType().newInstance();
        method.getReturnType().getDeclaredMethod("setId", Long.class).invoke(ret, args[0]);
        return ret;
    }
}
