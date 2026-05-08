package com.example212306164.helloserver.controller;

import com.example212306164.helloserver.common.Result;
import com.example212306164.helloserver.dto.ChatRequestDTO;
import com.example212306164.helloserver.service.ChatService;
import com.example212306164.helloserver.vo.ChatResponseVO;
import org.springframework.web.bind.annotation.*;

/**
 * 聊天控制器，提供基础聊天接口
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Result<ChatResponseVO> chat(@RequestBody ChatRequestDTO requestDTO) {
        String answer = chatService.chat(requestDTO.getMessage());
        ChatResponseVO responseVO = new ChatResponseVO(requestDTO.getMessage(), answer);
        return Result.success(responseVO);
    }
}