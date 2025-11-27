package com.andrew.smartfitnessassistant.service;

import com.andrew.smartfitnessassistant.common.RoleEnum;
import com.andrew.smartfitnessassistant.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class AuthenticateService {

    private final Map<String, AdminSession> adminSessions = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;


    @Value("${admin.session-timeout-minutes:120}")
    private int sessionTimeoutMinutes;

    public AuthenticateService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticateAdmin(UserEntity user, String password) {
        String chatId = user.getTelegramChatId();
        if (user.getRole() != RoleEnum.ROLE_ADMIN) {
            log.warn("User {} attempted admin login but doesn't have ADMIN role", chatId);
            return false;
        }

        if (user.getPassword() == null) {
            log.warn("Admin user {} has no password set", chatId);
            return false;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Invalid password for admin user: {}", chatId);
            return false;
        }

        AdminSession session = new AdminSession(
                chatId,
                Instant.now().plus(Duration.ofMinutes(sessionTimeoutMinutes))
        );
        adminSessions.put(chatId, session);

        return true;
    }

    public boolean isAdminAuthenticated(String chatId) {
        AdminSession session = adminSessions.get(chatId);
        if (session == null) {
            return false;
        }

        if (session.getExpiresAt().isBefore(Instant.now())) {
            adminSessions.remove(chatId);
            return false;
        }

        return true;
    }

    public void logoutAdmin(String chatId) {
        adminSessions.remove(chatId);
        log.info("Admin logged out for chatId: {}", chatId);
    }

    public void cleanupExpiredSessions() {
        Instant now = Instant.now();
        adminSessions.entrySet().removeIf(entry ->
                entry.getValue().getExpiresAt().isBefore(now)
        );
    }

    @Getter
    @AllArgsConstructor
    public static class AdminSession {
        private String chatId;
        private Instant expiresAt;
    }
}
