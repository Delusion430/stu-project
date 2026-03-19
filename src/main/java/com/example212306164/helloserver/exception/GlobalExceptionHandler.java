package com.example212306164.helloserver.exception;

import com.example212306164.helloserver.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理所有未捕获的异常
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 记录日志（此处省略）
        return Result.error(500, "服务器内部错误：" + e.getMessage());
    }

    // 可以添加更具体的异常处理方法，例如：
    // @ExceptionHandler(ArithmeticException.class)
    // public Result<String> handleArithmetic(ArithmeticException e) {
    //     return Result.error(400, "算术异常：" + e.getMessage());
    // }
}