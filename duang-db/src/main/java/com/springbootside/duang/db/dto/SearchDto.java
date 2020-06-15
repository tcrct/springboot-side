package com.springbootside.duang.db.dto;

import javax.validation.constraints.NotEmpty;
import com.springbootside.duang.db.annotation.Param;

/**
 * 搜索条件对象
 *
 * @author Laotang
 * @since 1.0
 */
public class SearchDto implements java.io.Serializable {

    @NotEmpty
    @Param(name="field", label = "搜索字段", desc = "需要参与搜索的字段名称")
    private String field;

    @Param(name="operator", label = "表达式", desc = "==, >, >= , <, <=, !=, like, in, nin等, 默认为 ==")
    private String operator;

    @NotEmpty
    @Param(name="value", label = "搜索值", desc = "需要参与搜索的字段值")
    private Object value;


    public SearchDto() {
    }

    public SearchDto(String field, String operator, Object value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SearchDto{" +
                "field='" + field + '\'' +
                ", operator='" + operator + '\'' +
                ", value=" + value +
                '}';
    }

}
