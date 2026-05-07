package com.example212306164.helloserver.controller;

import com.example212306164.helloserver.common.Result;
import com.example212306164.helloserver.dto.UserDTO;
import com.example212306164.helloserver.entity.UserInfo;
import com.example212306164.helloserver.service.UserService;
import com.example212306164.helloserver.vo.UserDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 注册接口（POST /api/users）—— 无需 Token，在拦截器中放行
    @PostMapping
    public Result<String> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    // 登录接口（POST /api/users/login）—— 无需 Token，在 WebConfig 中排除
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    // 获取用户信息（GET /api/users/{id}）—— 需要 Token
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    // 删除用户（DELETE /api/users/{id}）—— 需要 Token，并执行真实删除
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") Long id) {
        return userService.deleteUser(id); // ✅ 调用 service 执行真实删除
    }

    // 分页查询（GET /api/users/page）—— 需要 Token
    @GetMapping("/page")
    public Result<Object> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        return userService.getUserPage(pageNum, pageSize);
    }

    // 5. 查询用户详情（多表联查 + Redis）
    @GetMapping("/{id}/detail")
    public Result<UserDetailVO> getUserDetail(@PathVariable("id") Long userId) {
        return userService.getUserDetail(userId);
    }

    // 6. 更新用户扩展信息
    @PutMapping("/{id}/detail")
    public Result<String> updateUserInfo(@PathVariable("id") Long userId,
                                         @RequestBody UserInfo userInfo) {
        userInfo.setUserId(userId);
        return userService.updateUserInfo(userInfo);
    }

    // 7. 删除用户扩展信息
    @DeleteMapping("/{id}/detail")
    public Result<String> deleteUserInfo(@PathVariable("id") Long userId) {
        return userService.deleteUserInfo(userId);
    }
}