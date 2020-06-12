package com.springbootside.duang.common.utils;

import com.springbootside.duang.common.event.EventFactory;
import com.springbootside.duang.common.event.core.EventModel;

/**
 * 事件工具类
 * 用于解耦，将发送通知类的运作交由专门的事件监听器进行操作
 *
 * @author Laotang
 * @since 1.0
 */
public class EventKit {

    private static class EventKitHolder {
        private static final EventKit INSTANCE = new EventKit();
    }
    private EventKit() {
    }
    public static final EventKit duang() {
        clear();
        return EventKitHolder.INSTANCE;
    }
    /*****************************************************************************/

    private static Object _value;
    private static String _name;
    private static boolean _isAsync;

    private static void clear() {
        _value = null;
        _name = "";
        _isAsync = false;
    }

    /**
     * 发送的内容
     * @param value
     * @return
     */
    public EventKit value(Object value) {
        _value = value;
        return this;
    }

    /**
     * 是否同步处理，默认为同步，当值为true时为异步
     * @param async
     * @return
     */
    public EventKit isAsync(boolean async) {
        _isAsync = async;
        return this;
    }

    /**
     *  Listener注解设置的key值
     * @param name
     * @return
     */
    public EventKit listenerKey(String name) {
        _name = name;
        return this;
    }

    /**
     * 执行请求
     * @return
     */
    public <T> T execute() {
        if(ToolsKit.isEmpty(_value)) {
            throw new NullPointerException("value is null");
        }
        return EventFactory.getInstance().executeEvent(new EventModel.Builder().key(_name).value(_value).isSync(_isAsync).build());
    }


}
