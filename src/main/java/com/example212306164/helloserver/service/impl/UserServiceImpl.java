package com.example212306164.helloserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example212306164.helloserver.common.Result;
import com.example212306164.helloserver.common.ResultCode;
import com.example212306164.helloserver.dto.UserDTO;
import com.example212306164.helloserver.entity.User;
import com.example212306164.helloserver.entity.UserInfo;
import com.example212306164.helloserver.mapper.UserInfoMapper;
import com.example212306164.helloserver.mapper.UserMapper;
import com.example212306164.helloserver.service.UserService;
import com.example212306164.helloserver.vo.UserDetailVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private static final String CACHE_KEY_PREFIX = "user:detail:";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; // 注入 BCrypt 密码编码器

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // ─── 分页查询 ────────────────────────────────────────────────────────────────

    @Override
    public Result<Object> getUserPage(Integer pageNum, Integer pageSize) {
        Page<User> pageParam = new Page<>(pageNum, pageSize);
        Page<User> resultPage = userMapper.selectPage(pageParam, null);
        return Result.success(resultPage);
    }

    // ─── 注册 ────────────────────────────────────────────────────────────────────

    @Override
    public Result<String> register(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();

        // 1. 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        // 2. 构建实体，密码使用 BCrypt 加密后存储
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // ✅ 加密存储

        // 3. 插入数据库
        userMapper.insert(user);
        return Result.success("注册成功！");
    }

    // ─── 登录 ────────────────────────────────────────────────────────────────────

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

        // 3. 使用 BCrypt 校验密码（matches 方法自动比对哈希）
        if (!passwordEncoder.matches(password, dbUser.getPassword())) { // ✅ 安全比对
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        // 4. 生成模拟 Token（生产环境应替换为 JWT）
        String token = java.util.UUID.randomUUID().toString().replace("-", "");
        return Result.success("Bearer " + token);
    }

    // ─── 查询单个用户 ─────────────────────────────────────────────────────────────

    @Override
    public Result<String> getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        String userInfo = "ID: " + user.getId() + ", 用户名: " + user.getUsername();
        return Result.success(userInfo);
    }

    // ─── 删除用户 ─────────────────────────────────────────────────────────────────

    @Override
    public Result<String> deleteUser(Long id) {
        // 1. 先检查用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 2. 执行真实的数据库删除
        userMapper.deleteById(id);
        return Result.success("用户 ID " + id + " 删除成功");
    }

    // ─── 获取用户详情（带缓存）─────────────────────────────────────────────────

    @Override
    public Result<UserDetailVO> getUserDetail(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;

        // 1. 先查缓存
        String json = redisTemplate.opsForValue().get(key);
        if (json != null && !json.isBlank()) {
            try {
                UserDetailVO cacheVO = objectMapper.readValue(json, UserDetailVO.class);
                return Result.success(cacheVO);
            } catch (JsonProcessingException e) {
                // 缓存数据异常，则删除缓存，继续查数据库
                redisTemplate.delete(key);
            }
        }

        // 2. 查数据库
        UserDetailVO detail = userInfoMapper.getUserDetail(userId);
        if (detail == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 3. 写缓存
        try {
            redisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(detail),
                    10,
                    TimeUnit.MINUTES
            );
        } catch (JsonProcessingException e) {
            // 缓存写入失败不影响业务
        }

        return Result.success(detail);
    }

    // ─── 更新用户信息（更新后删除缓存）─────────────────────────────────────────

    @Override
    @Transactional
    public Result<String> updateUserInfo(UserInfo userInfo) {
        // 参数校验
        if (userInfo == null || userInfo.getUserId() == null) {
            return Result.error(ResultCode.PARAM_ERROR);
        }

        // 1. 更新数据库
        int rows = userInfoMapper.updateById(userInfo);
        if (rows == 0) {
            return Result.error(ResultCode.UPDATE_FAILED);
        }

        // 2. 删除缓存
        String key = CACHE_KEY_PREFIX + userInfo.getUserId();
        redisTemplate.delete(key);

        return Result.success("更新成功");
    }

    // ─── 删除用户信息（删除后删除缓存）─────────────────────────────────────────

    @Override
    @Transactional
    public Result<String> deleteUserInfo(Long userId) {
        // 参数校验
        if (userId == null) {
            return Result.error(ResultCode.PARAM_ERROR);
        }

        // 1. 删除数据库记录
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUserId, userId);
        int rows = userInfoMapper.delete(queryWrapper);
        if (rows == 0) {
            return Result.error(ResultCode.DELETE_FAILED);
        }

        // 2. 删除缓存
        String key = CACHE_KEY_PREFIX + userId;
        redisTemplate.delete(key);

        return Result.success("删除成功");
    }
}