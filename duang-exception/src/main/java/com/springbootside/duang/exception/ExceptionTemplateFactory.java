package com.springbootside.duang.exception;

import com.springbootside.duang.exception.common.AbstractExceptionTemplate;
import com.springbootside.duang.exception.dto.ExceptionResultDto;
import com.springbootside.duang.exception.template.MethodArgumentNotValidExceptionTemplate;
import com.springbootside.duang.exception.template.NullPointerExceptionTemplate;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laotang on 2020/5/23.
 */
public class ExceptionTemplateFactory {

    private static Map<Class, AbstractExceptionTemplate> EXCEPTION_TEMPLATE_MAP = new HashMap<>();

    static {
        init(MethodArgumentNotValidException.class, new MethodArgumentNotValidExceptionTemplate());
        init(NullPointerException.class, new NullPointerExceptionTemplate());
    }

    /**
     * 初始化异常模板类
     */
    private static void init(Class<?> clazz, AbstractExceptionTemplate template) {
        if (!EXCEPTION_TEMPLATE_MAP.containsKey(clazz) && null != template) {
            EXCEPTION_TEMPLATE_MAP.put(clazz, template);
        }
    }

    public static ExceptionResultDto handle(Exception exception) {
        AbstractExceptionTemplate exceptionTemplate = EXCEPTION_TEMPLATE_MAP.get(exception.getClass());
        if (null == exceptionTemplate) {
            return null;
        }
        exceptionTemplate.handle(exception);

        String exceptionName = exception.getClass().getName();
        System.out.println(exceptionName);

        ExceptionResultDto resultDto = new ExceptionResultDto();

        return resultDto;
    }


}
