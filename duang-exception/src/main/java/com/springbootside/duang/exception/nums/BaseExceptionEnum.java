package com.springbootside.duang.exception.nums;

/**
 * 基础异常信息
 *
 * @author Laotang
 * @version 1.0
 */
public enum  BaseExceptionEnum implements IExceptionEnum {

    /**参数为空*/
    PARAM_NULL(1000, "参数为空"),



    ;
    private Integer code;
    private String message;

    private BaseExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
