package com.springbootside.duang.common.dto;

import java.util.Objects;

public class R implements java.io.Serializable {

    private int code;
    private Object msg;

    public R() {

    }

    public R(int code, Object msg) {
        this.code = code;
        this.msg = msg;
    }

    public static R success() {
        return new R(0, "success");
    }
    public static R success(Object obj) {
        return new R(0, obj);
    }

    public static R error(int code, Object msg) {
        return new R(code, msg);
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
}
