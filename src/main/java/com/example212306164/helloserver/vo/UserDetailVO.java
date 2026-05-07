package com.example212306164.helloserver.vo;

import lombok.Data;

@Data
public class UserDetailVO {
    private Long userId;
    private String username;
    private String phone;
    private Integer age;
    private String address;
}