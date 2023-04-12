package com.example.config;

import com.example.controller.Interceptor.UserLoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * 配置拦截路径
 */

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new UserLoginInterceptor());
        registration.addPathPatterns("/**")
                .excludePathPatterns(
                        "/**/login",
                        "/**/logout",
                        "/**/enroll",
                        "/frame/**",
                        "/css/**",
                        "/images/**",
                        "/js/**",
                        "/register.html",
                        "/login.html",
                        "/adminLogin.html",
                        "/page/index.html",
                        "/page/search.html/**",
                        "/product/**",
                        "/page/product.html"
                );
    }

    /**
     * 放行静态路径
     *
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }
}
