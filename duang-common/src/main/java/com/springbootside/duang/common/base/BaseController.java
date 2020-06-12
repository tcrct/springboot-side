package com.springbootside.duang.common.base;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpStatus;
import com.springbootside.duang.common.utils.ToolsKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.Validation;
import java.io.BufferedReader;
import java.util.Optional;

import static cn.hutool.core.text.csv.CsvUtil.getReader;

/**
 * Created by laotang on 2020/6/12.
 */
public abstract class BaseController<T> {

    private Class<T> clazz;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    protected String getValue(String name) {
        String param = request.getParameter(name);
        if (ToolsKit.isEmpty(param)) {
            param = (String)request.getAttribute(name);
        }
        return param;
    }

    protected Integer getIntValue(String name) {
        String tempValue = getValue(name);
        try {
            return optional(tempValue).isPresent() ? Integer.valueOf(tempValue) : null;
        } catch (Exception e) {

        }
    }

    protected <T> T getBean() {
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            if(!optional(reader).isPresent()) {

            }
            String body = IoUtil.read(reader);
            if (ContentType.JSON.getValue().startsWith(request.getHeader(Header.CONTENT_TYPE.getValue()))) {
                return (T)ToolsKit.jsonParseObject(body, clazz);
            }
        } catch (Exception e) {

        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (Exception e) {

                }
            }
        }
    }

    private <T> Optional<T> optional(T obj) {
        return Optional.ofNullable(obj);
    }



    @RequestMapping(value = "/save", method= RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String save() {
        T vo = getBean();
        

        ValidationUtils.invokeValidator(new Validator());


     return null;
    }


}
