package com.springbootside.duang.common.utils;

import com.springbootside.duang.common.dto.HeadDto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 封装ThreadLocal
 *
 * @param <T>
 *
 * @author Laotang
 * @since 1.0
 */
public abstract class DuangThreadLocal<T> {

    private final Map<Thread, T> duangThreadLocalMap = Collections.synchronizedMap(new HashMap<>());
    private final static int MAX_THREAD_NUMBER = 20;

    public void set(T value) {
        Thread thread = Thread.currentThread();
        reset(thread, value);
        duangThreadLocalMap.put(thread, value);
    }

    public T get() {
        Thread thread = Thread.currentThread();
        T value = duangThreadLocalMap.get(thread);
        if (null == value && !duangThreadLocalMap.containsKey(thread)) {
            value = initialValue();
            duangThreadLocalMap.put(thread, value);
        }
        return value;
    }

    public void remove() {
        duangThreadLocalMap.remove(Thread.currentThread());
    }

    private void reset(Thread thread, T value) {
        synchronized (this) {
            if (duangThreadLocalMap.size() > MAX_THREAD_NUMBER) {
                for (Iterator<Map.Entry<Thread, T>> iterator = duangThreadLocalMap.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<Thread, T> entry = iterator.next();
                    T threadLocalObject = entry.getValue();
                    // 如果是HeadDto，则判断存放时长是否大于3秒(默认3秒内返回)，如果是则清除
                    if (threadLocalObject instanceof HeadDto) {
                        HeadDto headDto = (HeadDto) threadLocalObject;
                        String requestId = headDto.getRequestId();
                        if (ToolsKit.isNotEmpty(requestId)) {
                            DuangId duangId = new DuangId(headDto.getRequestId());
                            if ((System.currentTimeMillis() - duangId.getTime()) > 3000L) {
                                iterator.remove();
                            }
                        } else {
                            iterator.remove();
                        }
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
    }

    protected abstract T initialValue() ;

}
