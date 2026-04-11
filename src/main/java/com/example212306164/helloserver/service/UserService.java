package com.example212306164.helloserver.service;

import com.example212306164.helloserver.common.Result;
import com.example212306164.helloserver.dto.UserDTO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
    Result<String> getUserById(Long id);
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);
}