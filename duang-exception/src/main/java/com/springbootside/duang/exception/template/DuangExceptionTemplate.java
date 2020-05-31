package com.springbootside.duang.exception.template;


import com.springbootside.duang.exception.common.AbstractExceptionTemplate;
import com.springbootside.duang.exception.common.DuangException;
import com.springbootside.duang.exception.dto.ExceptionResultDto;
import com.springbootside.duang.exception.utils.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常枚举处理模板
 *
 * @author Laotang
 * @version 1.0
 */
public class DuangExceptionTemplate extends AbstractExceptionTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(DuangExceptionTemplate.class);

    @Override
    public Class<?> exceptionClass() {
        return DuangException.class;
    }

    @Override
    public ExceptionResultDto handle(Exception exception) {
        LOGGER.info("异常枚举抛出异常");
        DuangException duangException = (DuangException) exception;
        ExceptionResultDto exceptionResultDto =  new ExceptionResultDto();
        exceptionResultDto.setCode(duangException.getCode());
        exceptionResultDto.setMessage(duangException.getMessage());
        exceptionResultDto.setStackMsg(Exceptions.getStackTraceAsString(exception));
        LOGGER.info(exceptionResultDto.getStackMsg());
        return exceptionResultDto;
    }

}
