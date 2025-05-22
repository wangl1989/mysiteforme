package com.mysiteforme.admin.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatDTO implements Serializable {

    private String chatId;

    private String type;

    private String text;

    private String time;
}
