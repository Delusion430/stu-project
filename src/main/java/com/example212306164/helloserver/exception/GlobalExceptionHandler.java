package com.example212306164.helloserver.exception;

import com.example212306164.helloserver.common.Result;
import com.example212306164.helloserver.common.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理所有未捕获的异常
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        e.printStackTrace(); // 打印堆栈，便于调试
        return Result.error(ResultCode.ERROR);
    }
}
