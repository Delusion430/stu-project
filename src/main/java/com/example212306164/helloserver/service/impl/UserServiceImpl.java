package com.example212306164.helloserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example212306164.helloserver.common.Result;
import com.example212306164.helloserver.common.ResultCode;
import com.example212306164.helloserver.dto.UserDTO;
import com.example212306164.helloserver.entity.User;
import com.example212306164.helloserver.mapper.UserMapper;
import com.example212306164.helloserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;   // 注入 UserMapper 替代 Map

    @Override
    public Result<String> register(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();

        // 1. 查询该用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        // 2. 搭装实体对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);   // 生产环境需加密，此处仅为演示

        // 3. 插入数据库
        userMapper.insert(user);

        return Result.success("注册成功！");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();

        // 1. 根据用户名查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User dbUser = userMapper.selectOne(queryWrapper);

        // 2. 校验用户是否存在
        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 3. 校验密码是否正确
        if (!dbUser.getPassword().equals(password)) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        // 4. 生成模拟 Token
        String token = UUID.randomUUID().toString().replace("-", "");
        return Result.success("Bearer " + token);
    }

    @Override
    public Result<String> getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        // 返回简单的用户信息（实际可返回 User 对象，但 Result<String> 限制，转为字符串）
        String userInfo = "ID: " + user.getId() + ", 用户名: " + user.getUsername();
        return Result.success(userInfo);
    }
}