package com.example212306164.helloserver.controller;

import com.example212306164.helloserver.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 查询用户（GET请求）
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        String data = "查询成功，正在返回 ID 为 " + id + " 的用户信息";
        return Result.success(data);
    }

    // 创建用户（POST请求）- 注册
    @PostMapping
    public Result<String> createUser(@RequestParam(required = false) String username,
                                     @RequestParam(required = false) String password,
                                     @RequestParam(required = false) String email,
                                     @RequestBody(required = false) String jsonBody) {

        String data;
        if (username != null) {
            // 表单方式
            data = "用户创建成功（表单），用户名：" + username;
        } else if (jsonBody != null && !jsonBody.isEmpty()) {
            // JSON方式
            data = "用户创建成功（JSON），数据：" + jsonBody;
        } else {
            data = "用户创建成功（无参数）";
        }

        return Result.success(data);
    }

    // 登录接口
    @PostMapping("/login")
    public Result<String> login(@RequestParam String username, @RequestParam String password) {
        String data = "登录成功，欢迎 " + username;
        return Result.success(data);
    }

    // 更新用户（PUT请求）- 敏感操作
    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable("id") Long id, @RequestBody String userInfo) {
        String data = "用户 ID " + id + " 更新成功";
        return Result.success(data);
    }

    // 删除用户（DELETE请求）- 敏感操作
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") Long id) {
        String data = "用户 ID " + id + " 删除成功";
        return Result.success(data);
    }
}