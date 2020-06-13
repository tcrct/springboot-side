package com.springbootside.duang.common.dto;

/**
 * 验证失败模型
 *
 * @author Laotang
 * @since
 */
public class ValidatorErrorDto implements java.io.Serializable {

    private String fieldName;
    private String errorMsg;

    public ValidatorErrorDto() {
    }

    public ValidatorErrorDto(String fieldName, String errorMsg) {
        this.fieldName = fieldName;
        this.errorMsg = errorMsg;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
