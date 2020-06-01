package com.springbootside.duang.common.config;

import com.springbootside.duang.common.ToolsKit;
import com.springbootside.duang.common.handler.HandlerFactory;
import io.undertow.util.HeaderMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 *
 * @author Laotang
 * @since 1.0
 */
public class DuangInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DuangInterceptor.class);

    /**
     * 前置拦截器
     * 预处理回调方法，若方法返回值为true，请求继续（调用下一个拦截器或处理器方法）；
     * 若方法返回值为false，请求处理流程中断，不会继续调用其他的拦截器或处理器方法，此时需要通过response产生响应；
     *
     * @param request   请求对象
     * @param response  返回对象
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            return HandlerFactory.handler(request, response);
        } catch (Exception e) {
            LOGGER.warn("框架在执行拦截器时出错: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 后处理回调方法，实现处理器的后处理（但在渲染视图之前），此时可以通过modelAndView对模型数据进行处理或对视图进行处理；
     * @param request   请求对象
     * @param response 返回对象
     * @param handler 处理器
     * @param modelAndView  视图对象
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        LOGGER.info("request uri：{}",request.getRequestURI());
        if (null != modelAndView) {
            LOGGER.info("result: " + ToolsKit.toJsonString(modelAndView.getModelMap()));
        }
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LOGGER.info("当前拦截的方法为：{}",handlerMethod.getMethod().getName());
            LOGGER.info("当前拦截的方法参数长度为：{}",handlerMethod.getMethod().getParameters().length);
            LOGGER.info("当前拦截的方法为：{}",handlerMethod.getBean().getClass().getName());
            LOGGER.info("开始拦截---------");
            String uri = request.getRequestURI();
            LOGGER.info("拦截的uri："+uri);
        }
    }


    /**
     * 整个请求处理完毕回调方法，即在视图渲染完毕时调用。
     * @param request   请求对象
     * @param response 返回对象
     * @param handler 处理器
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }
}
