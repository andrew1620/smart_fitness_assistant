package com.andrew.smartfitnessassistant.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UserDto {
    private String id;
    private UUID workoutPlanId;
    private UUID nutritionPlanId;

    private String surveyStatus;
    private Integer currentQuestionIndex;
    private Integer age;
    private Integer fat;
    private String water;
    private Integer height;
    private Integer weight;
    private Integer desiredWeight;
    private Integer trainingLevel;
    private String pressUp;
    private String lifting;
    private String trainingFrequency;
    private Integer time;
    private String aim;
    private String bodyType;
    private String desiredBody;
    private String workBody;
    private String mealPlan;
    private String junkFood;
    private String preferredSport;
    private String additionalAim;
    private String preferredTrainingSpace;
    private String inventory;
}
