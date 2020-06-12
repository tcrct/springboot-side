package com.springbootside.duang.common.event;

import cn.hutool.core.thread.GlobalThreadPool;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.springbootside.duang.common.annotation.Listener;
import com.springbootside.duang.common.event.core.Event;
import com.springbootside.duang.common.event.core.EventListener;
import com.springbootside.duang.common.event.core.EventModel;
import com.springbootside.duang.common.utils.SettingKit;
import com.springbootside.duang.common.utils.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  事件处理器工厂，用于处理事件，使程序解耦
 *
 * @author Loatang
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
public class EventFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventFactory.class);

    private static EventFactory eventFactory;
    private static ConcurrentHashMap<String, EventListener> eventListenerMap = new ConcurrentHashMap<>();

    public static EventFactory getInstance() {
        if(null == eventFactory) {
            eventFactory = new EventFactory();
        }
        return eventFactory;
    }

    private EventFactory() {
        String basePackage = SettingKit.duang().key("base.package").getString();
        if (ToolsKit.isEmpty(basePackage)) {
            LOGGER.info("请在配置文件中设置base.package值");
            return;
        }
        Set<Class<?>> listenerSet = ClassUtil.scanPackageByAnnotation(basePackage, Listener.class);
        if (ToolsKit.isEmpty(listenerSet)) {
            LOGGER.info("在[{}]包路径下没有发现注解为[{}]的类！", basePackage, Listener.class.getName());
            return;
        }
        List<Object> listenetBeanList = new ArrayList<>(listenerSet.size());
        try {
            for (Iterator<Class<?>> iterator = listenerSet.iterator(); iterator.hasNext(); ) {
                Class<?> listenerClass = iterator.next();
                if (ToolsKit.isEmpty(listenerClass)) {
                    continue;
                }
                listenetBeanList.add(ReflectUtil.newInstance(listenerClass));
            }
        } catch (Exception e) {
            LOGGER.info("将Listener类初始化时失败: {} {}", e.getMessage(), e);
            return;
        }
        if(ToolsKit.isNotEmpty(listenetBeanList)) {
            try {
                for (Object listener : listenetBeanList) {
                    EventListener eventEventListener = (EventListener) listener;
                    Listener listenerAnnot = eventEventListener.getClass().getAnnotation(Listener.class);
                    String key = listenerAnnot.name();
                    if(ToolsKit.isEmpty(key)) {
                        key = eventEventListener.getClass().getName();
                    }
                    eventListenerMap.put(key, eventEventListener);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
    public <T> T executeEvent(EventModel model){
        String key = model.getKey();
        EventListener eventListener = eventListenerMap.get(key);
        if(ToolsKit.isEmpty(eventListener)){
            throw new NullPointerException("find eventListener["+key+"] is null");
        }
        Event event = new Event(model.getModel());
        return exceute(eventListener,event, model.isAsync());
    }

    @SuppressWarnings("unchecked")
    private <T> T exceute(final EventListener eventListener, final Event event, final boolean aync) {
//		Type type = ((ParameterizedType) eventListener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
        if(aync){
            ThreadUtil.execAsync(new Thread(){
                public void run() {
                    eventListener.onEvent(event);
                }
            });
            return (T)null;		//如果是异步的话，就直接返回null;
        } else {
            return (T) eventListener.onEvent(event);
        }
    }
}
