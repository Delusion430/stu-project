package com.example212306164.helloserver.controller;

import com.example212306164.helloserver.common.Result;
import com.example212306164.helloserver.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 模拟一个简单的内存存储，实际开发中会注入Service
    // 为了演示，此处直接返回结果

    // 查询用户（GET）
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        // 故意制造异常测试全局异常处理（例如除以0）
        int a = 1 / 0;  // 取消注释可触发异常
        return Result.success("查询成功，正在返回 ID 为 " + id + " 的用户信息");
    }

    // 新增用户（POST）
    @PostMapping
    public Result<String> createUser(@RequestBody User user) {
        // 模拟保存用户，返回成功信息
        return Result.success("新增成功，接收到用户：" + user.getName() + "，年龄：" + user.getAge());
    }

    // 全量更新用户（PUT）
    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        return Result.success("更新成功，ID " + id + " 的用户已修改为：" + user.getName());
    }

    // 删除用户（DELETE）
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") Long id) {
        return Result.success("删除成功，已移除 ID 为 " + id + " 的用户");
    }
}