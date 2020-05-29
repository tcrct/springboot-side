package com.springbootside.duang.exception.common;

import com.springbootside.duang.exception.dto.ExceptionResultDto;

/**
 * Created by laotang on 2020/5/23.
 */
public abstract class AbstractExceptionTemplate {

    protected Exception exception;

    public abstract Class<?> exceptionClass();
    public abstract ExceptionResultDto handle(Exception exception);

}