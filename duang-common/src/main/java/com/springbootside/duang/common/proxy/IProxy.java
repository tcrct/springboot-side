package com.springbootside.duang.common.proxy;

public interface IProxy {
    /**
     * 执行链式代理
     *
     * @param proxyChain 代理链
     * @return 目标方法返回值
     * @throws Exception 异常
     */
    Object doProxy(ProxyChain proxyChain) throws Exception;
}
