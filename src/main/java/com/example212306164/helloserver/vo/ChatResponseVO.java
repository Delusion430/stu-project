package com.example212306164.helloserver.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天响应VO，用于返回用户问题和大模型回答
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseVO {
    private String question;
    private String answer;
}