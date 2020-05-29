package com.springbootside.duang.exception.template;


import com.springbootside.duang.exception.common.AbstractExceptionTemplate;
import com.springbootside.duang.exception.dto.ExceptionResultDto;

/**
 * Created by laotang on 2020/5/23.
 */
public class NullPointerExceptionTemplate extends AbstractExceptionTemplate {

    @Override
    public Class<?> exceptionClass() {
        return NullPointerException.class;
    }

    @Override
    public ExceptionResultDto handle(Exception exception) {
        NullPointerException nullPointerException = (NullPointerException) exception;
        return new ExceptionResultDto();
    }

}
