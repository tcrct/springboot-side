package com.springbootside.duang.exception.common;

import com.springbootside.duang.common.ToolsKit;
import com.springbootside.duang.exception.ExceptionTemplateFactory;
import com.springbootside.duang.exception.dto.ExceptionResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 全局异常处理器
 * 业务代码抛出异常后，统一由该类进行处理，以确保所有异常信息以正确的方式返回到请求端
 *
 * @author Laotang
 * @version 1.0
 */
@ControllerAdvice
@Component
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle(Exception exception) {

        if (ToolsKit.isEmpty(exception)) {
            throw new NullPointerException("exception is null!");
        }

        System.out.println(exception.getClass().getName());
        System.out.println(exception.getClass());

        ExceptionResultDto resultDto = ExceptionTemplateFactory.handle(exception);

        return "bad request222333" ;
    }
}
