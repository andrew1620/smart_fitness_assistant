package com.andrew.smartfitnessassistant.service;

import com.andrew.smartfitnessassistant.dto.event.SendMessageEvent;
import com.andrew.smartfitnessassistant.entity.UserEntity;
import com.andrew.smartfitnessassistant.interceptor.TelegramSecurityInterceptor;
import com.andrew.smartfitnessassistant.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class BotService extends TelegramLongPollingBot {

    private final ProcessMessagesService processMessagesService;
    private final MessageOutputService messageOutputService;
    private final TelegramSecurityInterceptor securityInterceptor;
    private final AdminCommandService adminCommandService;

    private static final String START = "/start";
    private static final String SET_REMIND = "/setRemind";
    private static final String SET_REMIND_HELP = "/setRemindHelp";
    private static final String ADMIN_LOGIN = "/login";
    private static final String HELP = "/help";
    private static final String ADMIN_LOGOUT = "/admin_logout";

    public BotService(@Value("${bot.token}") String token,
                      ProcessMessagesService processMessagesService,
                      MessageOutputService messageOutputService, TelegramSecurityInterceptor securityInterceptor,
                      AdminCommandService adminCommandService) {
        super(token);
        this.processMessagesService = processMessagesService;
        this.messageOutputService = messageOutputService;
        this.securityInterceptor = securityInterceptor;
        this.adminCommandService = adminCommandService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String message = update.getMessage().getText();
        String chatId = String.valueOf(update.getMessage().getChatId());

        securityInterceptor.processWithSecurity(chatId, () -> {
            UserEntity user = SecurityUtils.getCurrentUserOrThrow();

            if (message.equals(START)) {
                processMessagesService.processStart(user, update);
            } else if (message.equals(SET_REMIND_HELP)) {
                processMessagesService.processSetRemindHelp(user);
            } else if (message.startsWith(SET_REMIND)) {
                processMessagesService.processSetRemind(user, message);
            } else if (message.startsWith(ADMIN_LOGIN)) {
                processMessagesService.processAdminLogin(user, message);
            } else if (message.equals(ADMIN_LOGOUT)) {
                processMessagesService.processAdminLogout(user);
            } else if (message.equals(HELP)) {
                processMessagesService.processHelp(user);
            } else if (message.startsWith("/admin_")) {
                adminCommandService.processAdminCommand(user, message);
            } else {
                processMessagesService.processDefault(user, message);
            }
        });
    }

    @EventListener
    public void handleSendMessageEvent(SendMessageEvent event) {
        SendMessage sendMessage = new SendMessage(event.getChatId(), event.getMessage());

        if (event.getListForKeyboard() != null) {
            sendMessage.setReplyMarkup(messageOutputService.getKeyboardForList(event.getListForKeyboard()));
        }

        executeMessage(sendMessage);
    }

    @Override
    public String getBotUsername() {
        return "SmartFitnessAssistantBot";
    }

    private void executeMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
