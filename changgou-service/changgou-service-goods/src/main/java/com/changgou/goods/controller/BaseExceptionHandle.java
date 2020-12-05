package com.changgou.goods.controller;

import com.changgou.goods.entity.Result;
import com.changgou.goods.entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// RestBody和 ControllerAdvice 组合注解 用于定义全局性的东西
@RestControllerAdvice
public class BaseExceptionHandle {
    // 用于定义controller级别的异常 若controller抛出异常 则会调用这个方法
    @ExceptionHandler(value = Exception.class)
    public Result exceptionResult(Exception e) {
        e.printStackTrace();

        return new Result(false, StatusCode.ERROR, "系统错误");
    }
}
