package com.springbootside.duang.exception.template;


import com.springbootside.duang.exception.common.AbstractExceptionTemplate;
import com.springbootside.duang.exception.dto.ExceptionResultDto;
import com.springbootside.duang.exception.utils.Exceptions;
import org.beetl.sql.core.BeetlSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类转换 异常处理
 *
 * @author Laotang
 * @version 1.0
 */
public class ClassCastExceptionTemplate extends AbstractExceptionTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassCastExceptionTemplate.class);

    @Override
    public Class<?> exceptionClass() {
        return ClassCastException.class;
    }

    @Override
    public ExceptionResultDto handle(Exception exception) {
        ClassCastException e = (ClassCastException) exception;
        ExceptionResultDto exceptionResultDto =  new ExceptionResultDto();
        exceptionResultDto.setCode(1);
        exceptionResultDto.setMessage("类转换异常: " + e.getMessage());
        exceptionResultDto.setStackMsg(Exceptions.getStackTraceAsString(exception));
        LOGGER.info(exceptionResultDto.getStackMsg());
        return exceptionResultDto;
    }

}
