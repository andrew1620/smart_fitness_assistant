package com.andrew.smartfitnessassistant.repository;

import com.andrew.smartfitnessassistant.entity.WorkoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkoutRepository extends JpaRepository<WorkoutEntity, UUID> {
}
