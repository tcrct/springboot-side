package com.springbootside.duang.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 返回结果头部信息内容
 *
 * @author Laotang
 * @since 1.0
 */
public class HeadDto implements java.io.Serializable {

    /** 错误码(非0值则为异常返回) */
    private int code;
    /** 异常信息 */
    private Object msg;
    /** 请求ID */
    private String requestId;
    /** 请求地址 */
    private String uri;
    /** 请求时间 */
    private String requestTime;
    /**处理时间(毫秒)*/
    private long processTime;
    /**开始时间*/
    @JsonIgnore
    private long startTime = System.currentTimeMillis();

    public HeadDto() {
    }

    public HeadDto(int code, String msg, String requestId, String uri, String requestTime, int processTime) {
        this.code = code;
        this.msg = msg;
        this.requestId = requestId;
        this.uri = uri;
        this.requestTime = requestTime;
        this.processTime = processTime;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

    public long getStartTime() {
        return startTime;
    }
}
