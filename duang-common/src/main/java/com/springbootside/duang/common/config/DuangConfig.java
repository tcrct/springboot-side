package com.springbootside.duang.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 配置类
 *
 * @author Loatang
 * @since 1.0
 */
@Configuration
public class DuangConfig extends WebMvcConfigurerAdapter {


    /**
     * 添加自定义的拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DuangInterceptor()).addPathPatterns("/**");
    }


}
