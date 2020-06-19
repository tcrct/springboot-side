package com.springbootside.duang.common.utils;

import com.springbootside.duang.common.proxy.ProxyChain;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DaoProxyKit implements MethodInterceptor {

    private DaoProxyKit() {

    }

    public static Object newProxyInstance(Class<?> targetClass) {
//        Enhancer enhancer = new Enhancer();
//        enhancer.setSuperclass(targetClass);
//        enhancer.setCallback(new DaoProxyKit());
//        return enhancer.create();

        return Enhancer.create(targetClass, new net.sf.cglib.proxy.MethodInterceptor() {
            @Override
            public Object intercept(Object targetObject, Method targetMethod, Object[] methodParams,
                                    net.sf.cglib.proxy.MethodProxy methodProxy) throws Throwable {
                System.out.println("###############: " + targetMethod.getName());
                return methodProxy.invokeSuper(targetObject, methodParams);
            }
        });

//        return java.lang.reflect.Proxy.newProxyInstance(targetClass.getClassLoader(), targetClass.getInterfaces(), new InvocationHandler() {
//            @Override
//            public Object invoke(Object targetObject, Method targetMethod, Object[] objects) throws Throwable {
//                System.out.println("###############: " + targetMethod.getName());
//                System.out.println(targetObject.getClass().getSuperclass());
//                for (Object obj : objects) {
//                    System.out.println("@@@@@@@@@@@:" + obj.getClass());
//                }
//                return targetMethod.invoke(targetObject, objects);
//                //return new ProxyChain(targetClass, targetObject, targetMethod, null, args, proxyList, originObj).doProxyChain();
//            }
//        });
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println(o.getClass());
        System.out.println(method.getName());
        for (Object obj : objects) {
            System.out.println(obj.getClass());
        }
        System.out.println(methodProxy.getSuperName());
        return methodProxy.invokeSuper(o, objects);
    }

}
