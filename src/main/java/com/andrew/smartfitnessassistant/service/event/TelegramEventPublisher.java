package com.andrew.smartfitnessassistant.service.event;

import com.andrew.smartfitnessassistant.dto.event.SendMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public void sendMessage(String chatId, String message) {
        eventPublisher.publishEvent(new SendMessageEvent(chatId, message, null));
    }

    public void sendMessageWithKeyboard(String chatId, String message, List<String> listForKeyboard) {
        eventPublisher.publishEvent(new SendMessageEvent(chatId, message, listForKeyboard));
    }
}
