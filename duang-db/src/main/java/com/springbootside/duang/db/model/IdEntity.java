package com.springbootside.duang.db.model;

import java.io.Serializable;

/**
 * 所有Entity类的基类
 *
 * @author Laotang
 * @version 1.0
 */
public class IdEntity implements java.io.Serializable {

    public final static String ID_FIELD = "id";

    private Serializable id;

    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }
}
