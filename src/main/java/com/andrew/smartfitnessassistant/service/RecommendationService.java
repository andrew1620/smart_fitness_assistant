package com.andrew.smartfitnessassistant.service;

import com.andrew.smartfitnessassistant.dto.UserDto;
import com.andrew.smartfitnessassistant.entity.NutritionPlanEntity;
import com.andrew.smartfitnessassistant.entity.UserEntity;
import com.andrew.smartfitnessassistant.entity.WorkoutPlanEntity;
import com.andrew.smartfitnessassistant.service.event.TelegramEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final UserService userService;
    private final NutritionPlanService nutritionPlanService;
    private final WorkoutPlanService workoutPlanService;
    private final MessageOutputService messageOutputService;
    private final TelegramEventPublisher telegramEventPublisher;

    public void getRecommendationPlan(UserEntity user) {
        WorkoutPlanEntity workoutPlanEntity = workoutPlanService.generateWorkoutPlanByAim(user.getAim());
        WorkoutPlanEntity createdWorkoutPlan = workoutPlanService.createWorkoutPlan(workoutPlanEntity);
        NutritionPlanEntity nutritionPlanEntity = nutritionPlanService.generatePlan(user);
        NutritionPlanEntity createdNutritionPlan = nutritionPlanService.createNutritionPlan(nutritionPlanEntity);
        UserDto userDto = UserDto.builder().workoutPlanId(createdWorkoutPlan.getId())
                .nutritionPlanId(createdNutritionPlan.getId()).build();
        userService.updateUserById(user.getId(), userDto);
        String message = messageOutputService.planCreatedMessage(workoutPlanEntity, nutritionPlanEntity);
        telegramEventPublisher.sendMessage(user.getTelegramChatId(), message);
    }
}
