package com.springbootside.duang.common.handler;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.setting.Setting;
import cn.hutool.setting.SettingUtil;
import com.springbootside.duang.common.utils.ToolsKit;
import com.springbootside.duang.common.annotation.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 处理器工厂
 *
 * @author Laotang
 * @since 1.0
 */
public class HandlerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlerFactory.class);

    /**
     * 请求处理器集合
     */
    private static final List<IHandler> HANDLERS = new ArrayList<>();
    /**
     *是否已经扫描
     */
    private static boolean isScanHandlerClass;

    static {
        HANDLERS.add(new InitDuangHandler());
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public static boolean handler(HttpServletRequest request, HttpServletResponse response) {

        if (isScanHandlerClass) {
            LOGGER.info("系统中没有存在自定义的处理器，返回true，以继续往下执行代码！");
            return true;
        }

        if (HANDLERS.isEmpty()) {
            initHandler();
            isScanHandlerClass = true;
        }

        if (HANDLERS.isEmpty()) {
            LOGGER.info("系统中没有存在自定义的处理器，返回true，继续往下执行代码！");
            return true;
        }
        IHandler handler = null;
        try {
            for (Iterator<IHandler> iterator = HANDLERS.iterator(); iterator.hasNext(); ) {
                handler = iterator.next();
                boolean isNext = handler.handler(request, response);
                if (!isNext) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.info("执行自定义的请求拦截处理器[{}]时抛出异常: {}", handler.getClass().getName(), e.getMessage());
            return false;
        }
    }

    /**
     * 初始化请求拦截处理器
     * 扫描添加所有带有@Handler的类
     */
    private static void initHandler() {
        Setting setting = SettingUtil.get("application.properties");
        if (ToolsKit.isEmpty(setting)) {
            LOGGER.info("application.properties不存在");
            return;
        }
        String scanPackage = setting.get("base.package");
        Set<Class<?>> handlerSet = ClassUtil.scanPackageByAnnotation(scanPackage, Handler.class);
        if (ToolsKit.isEmpty(handlerSet)) {
            LOGGER.info("没有自定义的请求拦截处理器");
            return;
        }
        try {
            TreeMap<Integer, IHandler> handlerTreeMap = new TreeMap<>();
            for (Class<?> clazz : handlerSet) {
                if (!IHandler.class.equals(clazz.getInterfaces()[0])) {
                    throw new RuntimeException(String.format("[{}]没有实现[{}]接口，请检查！", clazz.getName(), Handler.class.getSimpleName()));
                }
                Handler handlerAnn = clazz.getAnnotation(Handler.class);
                if (null == handlerAnn) {
                    continue;
                }
                IHandler handler = (IHandler) ReflectUtil.newInstance(clazz);
                if (ToolsKit.isNotEmpty(handler)) {
                    handlerTreeMap.put(handlerAnn.sort(), handler);
                }
            }

            if(!handlerTreeMap.isEmpty()) {
                HANDLERS.addAll(handlerTreeMap.values());
            }
        } catch (Exception e) {
            LOGGER.warn("初始化请求拦截处理器时出错: {}，清空HANDLERS集合后退出", e.getMessage(), e);
            HANDLERS.clear();
        }
    }
}
