package com.springbootside.duang.exception.template;


import com.springbootside.duang.exception.common.AbstractExceptionTemplate;
import com.springbootside.duang.exception.dto.ExceptionResultDto;
import com.springbootside.duang.exception.utils.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * 方法参数验证异常处理模板
 *
 * @author Laotang
 * @version 1.0
 */
public class MethodArgumentNotValidExceptionTemplate extends AbstractExceptionTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodArgumentNotValidExceptionTemplate.class);

    @Override
    public Class<?> exceptionClass() {
        return MethodArgumentNotValidException.class;
    }

    @Override
    public ExceptionResultDto handle(Exception exception) {
        LOGGER.info("方法参数验证异常");
        MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) exception;
        StringBuilder exceptionStr = new StringBuilder();
        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                exceptionStr.append("[")
                        .append(fieldError.getField())
                        .append(":")
                        .append(fieldError.getDefaultMessage())
                        .append("];");
            }
        }
        System.out.println(exceptionStr);
        ExceptionResultDto exceptionResultDto =  new ExceptionResultDto();
        exceptionResultDto.setCode(1);
        exceptionResultDto.setMessage(exceptionStr.toString());
        exceptionResultDto.setStackMsg(Exceptions.getStackTraceAsString(exception));
        LOGGER.info(exceptionResultDto.getStackMsg());
        return exceptionResultDto;
    }
}
