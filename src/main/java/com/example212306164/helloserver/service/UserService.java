package com.example212306164.helloserver.service;

import com.example212306164.helloserver.common.Result;
import com.example212306164.helloserver.dto.UserDTO;
import com.example212306164.helloserver.entity.UserInfo;
import com.example212306164.helloserver.vo.UserDetailVO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
    Result<String> getUserById(Long id);
    Result<String> deleteUser(Long id);
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);
    
    Result<UserDetailVO> getUserDetail(Long userId);
    Result<String> updateUserInfo(UserInfo userInfo);
    Result<String> deleteUserInfo(Long userId);
}