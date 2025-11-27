package com.andrew.smartfitnessassistant.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SendMessageEvent {
    private String chatId;
    private String message;
    private List<String> listForKeyboard;
}
