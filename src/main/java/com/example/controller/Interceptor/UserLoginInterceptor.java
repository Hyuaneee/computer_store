package com.example.controller.Interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 利用uid进行拦截
 */


public class UserLoginInterceptor implements HandlerInterceptor {
    /**
     * controller调用前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        try {
            Number uid = (Number) request.getSession().getAttribute("uid");
            if (uid != null) {
                return true; //放行
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.sendRedirect("/login.html");
        return false;
    }

    /**
     * controller调用后，但视图还未渲染
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //System.out.println("执行登录拦截器的postHandle方法");
    }

    /**
     * 请求结束后调用
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //System.out.println("执行登录拦截器的afterCompletion方法");
    }
}
