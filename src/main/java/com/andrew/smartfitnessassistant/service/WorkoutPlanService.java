package com.andrew.smartfitnessassistant.service;

import com.andrew.smartfitnessassistant.entity.WorkoutPlanEntity;
import com.andrew.smartfitnessassistant.repository.WorkoutPlanRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkoutPlanService {
    private final WorkoutPlanRepository workoutPlanRepository;

    public WorkoutPlanEntity generateWorkoutPlanByAim(String aim) {
        String planId = switch(aim) {
            case "Снизить вес" -> "25d2f5e8-19b5-487b-8cb5-713bb4bfc6ab";
            case "Набрать мышечную массу" -> "21a6db2d-e773-4115-b537-cc7cf420410c";
            case "Проработать рельеф" -> "cd1e9f89-dda7-40f8-b95e-0260e92ac580";
            default -> throw new IllegalStateException("Unexpected value: " + aim);
        };

        WorkoutPlanEntity workoutPlan = workoutPlanRepository.findById(UUID.fromString(planId))
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        return workoutPlan;
    }

    public WorkoutPlanEntity createWorkoutPlan(WorkoutPlanEntity workoutPlan) {
        return workoutPlanRepository.save(workoutPlan);
    }

    public WorkoutPlanEntity findById(UUID workoutPlanId) {
        return workoutPlanRepository.findById(workoutPlanId).orElseThrow(() -> new NotFoundException("Workout not found"));
    }
}
