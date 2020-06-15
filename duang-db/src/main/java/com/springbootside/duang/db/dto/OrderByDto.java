package com.springbootside.duang.db.dto;

import com.springbootside.duang.db.annotation.Param;

public class OrderByDto implements java.io.Serializable {

    public final static String ASC_FIELD = "ASC";
    public final static String DESC_FIELD = "DESC";


    @Param(label = "排序的字段名")
    private String field;
    @Param(label = "排序的方向，分ASC/DESC")
    private String direction;

    public OrderByDto() {
    }

    public OrderByDto(String field, String direction) {
        this.field = field;
        this.direction = direction;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
