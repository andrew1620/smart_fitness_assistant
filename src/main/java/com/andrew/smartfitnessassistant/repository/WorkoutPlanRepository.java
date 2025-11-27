package com.andrew.smartfitnessassistant.repository;

import com.andrew.smartfitnessassistant.entity.WorkoutPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlanEntity, UUID> {

    @Override
    @Query("SELECT w from WorkoutPlanEntity w LEFT JOIN FETCH w.workouts WHERE w.id = :id ")
    Optional<WorkoutPlanEntity> findById(@Param("id") UUID id);
}
