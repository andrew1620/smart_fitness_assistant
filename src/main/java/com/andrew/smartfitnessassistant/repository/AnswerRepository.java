package com.andrew.smartfitnessassistant.repository;

import com.andrew.smartfitnessassistant.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<AnswerEntity, UUID> {
}
