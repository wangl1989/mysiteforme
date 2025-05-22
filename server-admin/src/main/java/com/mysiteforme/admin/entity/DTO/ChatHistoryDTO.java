package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ChatHistoryDTO implements Serializable {

    private String chatId;

    private List<ChatDTO> chatList;

}
