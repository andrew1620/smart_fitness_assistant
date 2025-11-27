package com.andrew.smartfitnessassistant.interceptor;

import com.andrew.smartfitnessassistant.common.RoleEnum;
import com.andrew.smartfitnessassistant.entity.UserEntity;
import com.andrew.smartfitnessassistant.service.AuthenticateService;
import com.andrew.smartfitnessassistant.service.TelegramAuthenticationToken;
import com.andrew.smartfitnessassistant.service.UserService;
import com.andrew.smartfitnessassistant.service.event.TelegramEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramSecurityInterceptor {

    private final UserService userService;
    private final AuthenticateService adminService;
    private final TelegramEventPublisher telegramEventPublisher;

    public void processWithSecurity(String chatId, Runnable commandProcessor) {


        UserEntity user = userService.getOrCreateUser(chatId);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_USER.name()));

        if (adminService.isAdminAuthenticated(chatId) && user.getRole() == RoleEnum.ROLE_ADMIN) {
            authorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_ADMIN.name()));
        }

        TelegramAuthenticationToken authentication =
                new TelegramAuthenticationToken(chatId, user, authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        try {
            commandProcessor.run();
        } catch (AccessDeniedException e) {
            handleAccessDenied(chatId, e);
        } catch (Exception e) {
            handleGenericError(chatId, e);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void handleAccessDenied(String chatId, AccessDeniedException e) {
        log.warn("Access denied for chatId: {}", chatId);

        String message = """
            🔒 *Доступ запрещен*
            
            Для выполнения этой команды требуются права администратора.
            
            💡 Используйте команду:
            `/login <пароль>`
            
            ❓ Если вы администратор, проверьте правильность пароля и срок действия сессии.
            """;

        telegramEventPublisher.sendMessage(chatId, message);
    }

    private void handleGenericError(String chatId, Exception e) {
        log.error("Error processing command for chatId: {}", chatId, e);

        String message = """
            ❌ *Произошла ошибка*
            
            Пожалуйста, попробуйте позже или обратитесь к администратору.
            
            🔄 Вы также можете попробовать команду снова.
            """;

        telegramEventPublisher.sendMessage(chatId, message);
    }
}
