package com.scu.imageseg.controller;

import com.scu.imageseg.exception.ServiceException;
import com.scu.imageseg.entity.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * @author wlt
 * @date 2024/6/14
 */

@Slf4j
@RestControllerAdvice // 该注解说明 所有Controller层的抛出的异常都会被拦截到这个类处理
public class GlobalExceptionHandler {
    /**
     * 自定义异常拦截器
     * @param request
     * @param e
     * @return {@link JSONResult}
     */
    @ResponseBody
    @ExceptionHandler(value = ServiceException.class) // 指定处理ServiceException的逻辑
    public JSONResult<Object> exceptionHandler(HttpServletRequest request, ServiceException e){
        log.error("发送{}异常",e.getMessage());
        return new JSONResult<>(e.getCode(), e.getLocalizedMessage());
    }
}
