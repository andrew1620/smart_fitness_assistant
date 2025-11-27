package com.andrew.smartfitnessassistant.service;

import com.andrew.smartfitnessassistant.entity.NutritionPlanEntity;
import com.andrew.smartfitnessassistant.entity.UserEntity;
import com.andrew.smartfitnessassistant.repository.NutritionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NutritionPlanService {

    private final NutritionPlanRepository nutritionPlanRepository;

    public NutritionPlanEntity generatePlan(UserEntity user) {
        // Базовый метаболизм (формула Миффлина-Сан Жеора)
        Double bmr = calculateBMR(user);

        // Общий расход калорий с учетом активности
        Double tdee = calculateTDEE(bmr, 1.55);

        // Целевые калории в зависимости от цели
        Double targetCalories = calculateTargetCalories(tdee, user.getAim());

        // Расчет БЖУ
        return calculateMacros(targetCalories, user.getWeight(), user.getAim());
    }

    private Double calculateBMR(UserEntity user) {
        // Формула Миффлина-Сан Жеора для мужчин
        return 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5;
    }

    private Double calculateTDEE(Double bmr, Double activityMultiplier) {
        // Коэффициент активности
        return bmr * activityMultiplier;
    }

    private Double calculateTargetCalories(Double tdee, String aim) {
        return switch(aim) {
            case "Снизить вес" -> tdee - 500; // дефицит 500 ккал
            case "Набрать мышечную массу" -> tdee + 300; // профицит 300 ккал
            case "Проработать рельеф" -> tdee - 300; // небольшой дефицит
            default -> tdee;
        };
    }

    private NutritionPlanEntity calculateMacros(Double calories, Integer weight, String aim) {
        NutritionPlanEntity plan = new NutritionPlanEntity();
        plan.setCalories(calories.intValue());

        // Расчет белков (г)
        Double protein = switch(aim) {
            case "Снизить вес" -> weight * 2.0; // 2г на кг веса
            case "Набрать мышечную массу" -> weight * 2.2; // 2.2г на кг веса
            case "Проработать рельеф" -> weight * 2.1; // 2.1г на кг веса
            default -> weight * 1.8;
        };

        // Расчет жиров (г) - 25% от калорий
        Double fat = (calories * 0.25) / 9;

        // Расчет углеводов (г) - оставшиеся калории
        Double proteinCalories = protein * 4;
        Double fatCalories = fat * 9;
        Double carbCalories = calories - proteinCalories - fatCalories;
        Double carbs = carbCalories / 4;

        plan.setProtein(protein.intValue());
        plan.setFat(fat.intValue());
        plan.setCarbs(carbs.intValue());

        return plan;
    }

    public NutritionPlanEntity createNutritionPlan(NutritionPlanEntity nutritionPlan) {
        return nutritionPlanRepository.save(nutritionPlan);
    }
}
