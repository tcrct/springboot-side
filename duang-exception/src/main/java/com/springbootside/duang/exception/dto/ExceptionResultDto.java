package com.springbootside.duang.exception.dto;

/**
 * 异常信息Dto
 *
 * @author Laotang
 * @version 1.0
 */
public class ExceptionResultDto implements java.io.Serializable {

    /**
     * 异常码
     */
    private int code;

    /**
     * 异常信息
     */
    private String message;

    /**
     * 异常堆栈信息
     */
    private String stackMsg;

    public ExceptionResultDto() {
    }

    public ExceptionResultDto(int code, String message, String stackMsg) {
        this.code = code;
        this.message = message;
        this.stackMsg = stackMsg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackMsg() {
        return stackMsg;
    }

    public void setStackMsg(String stackMsg) {
        this.stackMsg = stackMsg;
    }
}
