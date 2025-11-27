package com.andrew.smartfitnessassistant.repository;

import com.andrew.smartfitnessassistant.entity.RemindEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RemindRepository extends JpaRepository<RemindEntity, UUID> {
    List<RemindEntity> findAllByUserId(UUID userId);
}

