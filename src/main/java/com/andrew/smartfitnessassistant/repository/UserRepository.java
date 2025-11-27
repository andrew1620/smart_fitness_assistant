package com.andrew.smartfitnessassistant.repository;

import com.andrew.smartfitnessassistant.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    UserEntity findByTelegramChatId(String chatId);
}
