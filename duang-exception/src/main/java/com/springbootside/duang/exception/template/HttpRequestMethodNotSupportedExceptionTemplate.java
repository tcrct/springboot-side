package com.springbootside.duang.exception.template;


import com.springbootside.duang.exception.common.AbstractExceptionTemplate;
import com.springbootside.duang.exception.dto.ExceptionResultDto;
import com.springbootside.duang.exception.utils.Exceptions;
import org.beetl.sql.core.BeetlSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;

/**
 * BeetlSQL 异常处理
 *
 * @author Laotang
 * @version 1.0
 */
public class HttpRequestMethodNotSupportedExceptionTemplate extends AbstractExceptionTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestMethodNotSupportedExceptionTemplate.class);

    @Override
    public Class<?> exceptionClass() {
        return HttpRequestMethodNotSupportedException.class;
    }

    @Override
    public ExceptionResultDto handle(Exception exception) {
        HttpRequestMethodNotSupportedException e = (HttpRequestMethodNotSupportedException) exception;
        ExceptionResultDto exceptionResultDto =  new ExceptionResultDto();
        exceptionResultDto.setCode(1);
        exceptionResultDto.setMessage(e.getMessage());
        exceptionResultDto.setStackMsg(Exceptions.getStackTraceAsString(exception));
        LOGGER.info(exceptionResultDto.getStackMsg());
        return exceptionResultDto;
    }

}
