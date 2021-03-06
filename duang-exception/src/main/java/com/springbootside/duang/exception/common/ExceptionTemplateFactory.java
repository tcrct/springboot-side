package com.springbootside.duang.exception.common;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.springbootside.duang.exception.dto.ExceptionResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 异常模板处理工厂
 *
 * @author Laotang
 * @version 1.0
 */
public class ExceptionTemplateFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionTemplateFactory.class);

    // 异常模板集合，key为要处理的异常类，value为对应的处理模板
    private static Map<Class, AbstractExceptionTemplate> EXCEPTION_TEMPLATE_MAP = new HashMap<>();
    // 异常模板目录名称
    private static String EXCEPTION_TEMPLATE_SUB_PACKAGE = "template";

    /**
     * 初始化异常模板类
     */
    private static void init() {
        String packagePath = ExceptionTemplateFactory.class.getPackage().getName();
        packagePath = packagePath.substring(0, packagePath.indexOf("common"));
        packagePath += EXCEPTION_TEMPLATE_SUB_PACKAGE;
        Set<Class<?>> exceptionClassSet = ClassUtil.scanPackage(packagePath);
        if (null == exceptionClassSet) {
            LOGGER.info("根据包路径[{}]初始化异常模板类时出错，请检查！", packagePath);
        } else {
            for (Iterator<Class<?>> iterator = exceptionClassSet.iterator(); iterator.hasNext();) {
                Class<?> exceptionClass = iterator.next();
                if (null != exceptionClass) {
                    AbstractExceptionTemplate exceptionTemplate = (AbstractExceptionTemplate) ReflectUtil.newInstance(exceptionClass);
                    if (null != exceptionTemplate) {
                        EXCEPTION_TEMPLATE_MAP.put(exceptionTemplate.exceptionClass(), exceptionTemplate);
                    }
                }
            }
        }
    }

    /**
     * 处理捕捉到的异常
     * @param exception 抛出的异常
     * @return
     */
    public static ExceptionResultDto handle(Exception exception) {
        if (EXCEPTION_TEMPLATE_MAP.isEmpty()) {
            init();
        }
        AbstractExceptionTemplate exceptionTemplate = EXCEPTION_TEMPLATE_MAP.get(exception.getClass());
        if (null == exceptionTemplate) {
            LOGGER.info("[{}]异常处理模板类不存在，请检查！",exception.getClass());
            return null;
        }
        return exceptionTemplate.handle(exception);
    }

}
