package com.andrew.smartfitnessassistant.repository;

import com.andrew.smartfitnessassistant.entity.NutritionPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NutritionPlanRepository extends JpaRepository<NutritionPlanEntity, UUID> {
}
