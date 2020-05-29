package com.springbootside.duang.exception.template;


import com.springbootside.duang.exception.common.AbstractExceptionTemplate;
import com.springbootside.duang.exception.dto.ExceptionResultDto;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Created by laotang on 2020/5/23.
 */
public class MethodArgumentNotValidExceptionTemplate extends AbstractExceptionTemplate {

    @Override
    public Class<?> exceptionClass() {
        return MethodArgumentNotValidException.class;
    }

    @Override
    public ExceptionResultDto handle(Exception exception) {
        MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) exception;

        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                System.out.println("!!!!!!!!!! " + fieldError.getField() + "         " + fieldError.getDefaultMessage());
            }
            return new ExceptionResultDto();
        }
        return null;
    }
}
