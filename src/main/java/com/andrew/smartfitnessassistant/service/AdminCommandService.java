package com.andrew.smartfitnessassistant.service;

import com.andrew.smartfitnessassistant.common.RoleEnum;
import com.andrew.smartfitnessassistant.entity.UserEntity;
import com.andrew.smartfitnessassistant.service.event.TelegramEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminCommandService {

    private final UserService userService;
    private final TelegramEventPublisher telegramEventPublisher;
    private final MessageOutputService messageOutputService;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    public void processAdminCommand(UserEntity user, String command) {
        if (command.equals("/admin_users")) {
            showUsersList(user);
        } else if (command.equals("/admin_broadcast")) {
            telegramEventPublisher.sendMessage(user.getTelegramChatId(),
                    "Функция рассылки в разработке");
        } else if (command.startsWith("/admin_promote")) {
            promoteUserToAdmin(command, user);
        } else {
            telegramEventPublisher.sendMessage(user.getTelegramChatId(), messageOutputService.unknownCommandMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void showUsersList(UserEntity user) {
        List<UserEntity> users = userService.findAll();

        String message = messageOutputService.userListMessage(users);

        telegramEventPublisher.sendMessage(user.getTelegramChatId(), message);
    }

    @PreAuthorize("hasRole('ADMIN')")
    private void promoteUserToAdmin(String command, UserEntity user) {
        String[] parts = command.split(" ");
        if (parts.length < 3) {
            telegramEventPublisher.sendMessage(user.getTelegramChatId(),
                    "❌ Формат: /admin_promote <chatId> <пароль>");
            return;
        }

        String userId = parts[1];
        String password = parts[2];

        UserEntity targetUser = userService.findById(UUID.fromString(userId));
        String encodedPass = passwordEncoder.encode(password);
        userService.updateUserRoleAndPassword(targetUser.getId(), RoleEnum.ROLE_ADMIN, encodedPass);

        String messageForAdmin = messageOutputService.userPromotedToAdminMessage(userId);
        telegramEventPublisher.sendMessage(user.getTelegramChatId(), messageForAdmin);

        String messageForNewAdmin = messageOutputService.youPromotedToAdminMessage(password);
        telegramEventPublisher.sendMessage(targetUser.getTelegramChatId(), messageForNewAdmin);
    }
}
