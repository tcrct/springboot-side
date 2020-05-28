package com.springboot.demo.common;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器
 *
 * @author Laotang
 * @since 1.0
 */
@ControllerAdvice
@Component
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle(Exception exception) {

        if(exception instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException mnve = (MethodArgumentNotValidException) exception;

            BindingResult bindingResult = mnve.getBindingResult();
            if(bindingResult.hasErrors()){
                for (FieldError fieldError : bindingResult.getFieldErrors()) {
                    System.out.println("!!!!!!!!!! " + fieldError.getField()+"         "+ fieldError.getDefaultMessage());
                }
                return "fail";
            }
        }

        if(exception instanceof ConstraintViolationException){
            ConstraintViolationException exs = (ConstraintViolationException) exception;

            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                System.out.println("@@@@@@@@@@: " + item.getMessage());
            }
        }
        return "bad request, " ;
    }
}
