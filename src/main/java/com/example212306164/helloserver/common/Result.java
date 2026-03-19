package com.example212306164.helloserver.common;

public class Result<T> {
    private int code;       // 状态码，如200成功，500错误
    private String msg;     // 提示信息
    private T data;         // 返回的数据

    // 私有构造，防止直接创建
    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 成功响应（自定义消息和数据）
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    // 失败响应
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    // Getter 方法
    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public T getData() { return data; }
}