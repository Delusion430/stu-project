package com.example212306164.helloserver.dto;

import lombok.Data;

/**
 * 聊天请求DTO，用于接收用户输入的问题文本
 */
@Data
public class ChatRequestDTO {
    private String message;
}