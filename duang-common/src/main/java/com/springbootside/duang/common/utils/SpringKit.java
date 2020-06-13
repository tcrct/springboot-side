package com.springbootside.duang.common.utils;

import com.springbootside.duang.common.base.ICurdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * spring工具类 方便在非spring管理环境中获取bean
 * 
 * @author Laotang
 * @since 1.0
 */
@Component
public final class SpringKit implements BeanFactoryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringKit.class);

    /** Spring应用上下文环境 */
    private static ConfigurableListableBeanFactory beanFactory;
    /**ServiceImpl与泛型关系，key为泛型，value为Impl，用于CURD*/
    private static final Map<Class<?>, Object> serviceBeanMap = new HashMap<>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringKit.beanFactory = beanFactory;
        Map<String, Object> beanMap = beanFactory.getBeansWithAnnotation(Service.class);
        for (Iterator<Map.Entry<String, Object>> iterator = beanMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Object> entry = iterator.next();
            Object serviceImpl = entry.getValue();
            Class<?> genericTypeClass = ToolsKit.getSuperClassGenericType(serviceImpl.getClass(), 0);
            if (null == genericTypeClass) {
                LOGGER.warn("[{}]可能没有继承[{}]，不能自动生成CURD方法，请检查！", serviceImpl.getClass(), ICurdService.class.getName());
                continue;
            }
            serviceBeanMap.put(genericTypeClass, serviceImpl);
        }
    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException
     *
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 获取类型为requiredType的对象
     *
     * @param clz
     * @return
     * @throws BeansException
     *
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        T result = (T) beanFactory.getBean(clz);
        return result;
    }

    /**
     *  根据泛型取出ServiceImpl类
     * @param clz 泛型类，一般是entity/dto等类
     * @param <T>
     * @return
     * @throws BeansException
     */
    public static <T> T getBeanByGenericType(Class<T> clz) throws BeansException {
        return (T) serviceBeanMap.get(clz);
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @param name
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @param name
     * @return boolean
     * @throws NoSuchBeanDefinitionException
     *
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }

    /**
     * @param name
     * @return Class 注册对象的类型
     * @throws NoSuchBeanDefinitionException
     *
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     * @param name
     * @return
     * @throws NoSuchBeanDefinitionException
     *
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);
    }

    /**
     * 获取aop代理对象
     * 
     * @param invoker
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker)
    {
        return (T) AopContext.currentProxy();
    }
}
