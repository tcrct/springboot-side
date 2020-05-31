package com.springbootside.duang.exception.common;

/**
 * Created by laotang on 2020/5/31.
 */
public class DuangException extends RuntimeException {

    private Integer code;
    private String message;

    public DuangException() {
        super();
    }

    public DuangException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
