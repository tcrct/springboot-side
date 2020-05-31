package com.springbootside.duang.exception.nums;

import com.springbootside.duang.common.ToolsKit;
import com.springbootside.duang.exception.common.DuangException;

/**
 * 异常枚举接口
 *
 * @author Laotang
 * @version 1.0
 */
public interface IExceptionEnum {

    /**
     * 异常错误码
     * @return
     */
    Integer getCode();

    /**
     * 异常提示信息
     * @return
     */
    String getMessage();

    default void assertNotNull(Object object) {
        if (ToolsKit.isEmpty(object)) {
            throw new DuangException(getCode(), getMessage());
        }
    }

}
