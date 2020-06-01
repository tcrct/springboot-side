package com.springbootside.duang.common.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  处理器类接口
 *
 * @author Laotang
 * @since 1.0
 */
public interface IHandler {

    boolean handler(HttpServletRequest request, HttpServletResponse response);

}
