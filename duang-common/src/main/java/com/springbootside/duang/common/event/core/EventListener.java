package com.springbootside.duang.common.event.core;

/**
 * 事件监听接口
 *
 * @author Laotang
 * @since 1.0
 *
 * @param <T>
 */
public interface EventListener<T> extends java.util.EventListener {
    T onEvent(Event event);
}
