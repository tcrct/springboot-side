package com.springbootside.duang.common.entity;

import java.io.Serializable;

/**
 * 所有Entity类的基类
 *
 * @author Laotang
 * @version 1.0
 */
public class IdEntity implements java.io.Serializable {

    private Serializable id;

    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }
}
