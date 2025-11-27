package com.andrew.smartfitnessassistant.mapper;

import com.andrew.smartfitnessassistant.dto.UserDto;
import com.andrew.smartfitnessassistant.entity.QuestionEntity;


public class UserMapper {
    public static void updateUserDtoFromQuestion(QuestionEntity question, String answer, UserDto userDto) {
        switch (question.getTargetField()) {
            case "age" -> userDto.setAge(extractFirstNumber(answer));
            case "fat" -> userDto.setFat(extractFirstNumber(answer));
            case "height" -> userDto.setHeight(extractFirstNumber(answer));
            case "weight" -> userDto.setWeight(extractFirstNumber(answer));
            case "desired_weight" -> userDto.setDesiredWeight(extractFirstNumber(answer));
            case "training_level" -> userDto.setTrainingLevel(extractFirstNumber(answer));
            case "time" -> userDto.setTime(extractFirstNumber(answer));
            case "water" -> userDto.setWater(answer);
            case "press_up" -> userDto.setPressUp(answer);
            case "lifting" -> userDto.setLifting(answer);
            case "training_frequency" -> userDto.setTrainingFrequency(answer);
            case "aim" -> userDto.setAim(answer);
            case "body_type" -> userDto.setBodyType(answer);
            case "desired_body" -> userDto.setDesiredBody(answer);
            case "work_body" -> userDto.setWorkBody(answer);
            case "meal_plan" -> userDto.setMealPlan(answer);
            case "junk_food" -> userDto.setJunkFood(answer);
            case "preferred_sport" -> userDto.setPreferredSport(answer);
            case "additional_aim" -> userDto.setAdditionalAim(answer);
            case "preferred_training_space" -> userDto.setPreferredTrainingSpace(answer);
            case "inventory" -> userDto.setInventory(answer);
            default -> throw new IllegalArgumentException("Unknown target field: " + question.getTargetField());
        }
    }

    private static Integer extractFirstNumber(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            return null;
        }

        String trimmed = answer.trim();

        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("^\\d+").matcher(trimmed);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}
