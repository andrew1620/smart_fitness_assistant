package com.andrew.smartfitnessassistant.util;

import com.andrew.smartfitnessassistant.common.RoleEnum;
import com.andrew.smartfitnessassistant.entity.UserEntity;
import com.andrew.smartfitnessassistant.service.TelegramAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class SecurityUtils {

    public static Optional<UserEntity> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
                return Optional.of((UserEntity) authentication.getPrincipal());
            }
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Ошибка получения пользователя из SecurityContext: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public static UserEntity getCurrentUserOrThrow() {
        return getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));
    }

    public static Optional<String> getCurrentChatId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof TelegramAuthenticationToken) {
            return Optional.of(((TelegramAuthenticationToken) authentication).getChatId());
        }
        return Optional.empty();
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(RoleEnum.ROLE_ADMIN.name()));
    }

    public static Optional<Object> getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? Optional.of(authentication.getPrincipal()) : Optional.empty();
    }
}
