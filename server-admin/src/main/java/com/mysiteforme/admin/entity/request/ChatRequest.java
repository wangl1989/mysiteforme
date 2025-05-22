package com.mysiteforme.admin.entity.request;

import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {

    @NotBlank(message = MessageConstants.Chat.CHAT_ID_CAN_NOT_EMPTY)
    private String chatId;

    @NotBlank(message = MessageConstants.Chat.CONTENT_CAN_NOT_EMPTY)
    private String content;
}
