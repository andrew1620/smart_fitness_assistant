package com.andrew.smartfitnessassistant.service;

import com.andrew.smartfitnessassistant.common.RoleEnum;
import com.andrew.smartfitnessassistant.common.UserSurvayStatusEnum;
import com.andrew.smartfitnessassistant.dto.UserDto;
import com.andrew.smartfitnessassistant.entity.UserEntity;
import com.andrew.smartfitnessassistant.repository.UserRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserEntity getUserByTelegramChatId(String chatId) {
        return userRepository.findByTelegramChatId(chatId);
    }

    public UserEntity createUser(String chatId) {
        UserEntity userEntity = new UserEntity().setTelegramChatId(chatId).setCurrentQuestionIndex(0)
                .setSurveyStatus(UserSurvayStatusEnum.NOT_STARTED.toString()).setRole(RoleEnum.ROLE_USER);
        return userRepository.save(userEntity);
    }

    public UserEntity getOrCreateUser(String chatId) {
        UserEntity userEntity = getUserByTelegramChatId(chatId);
        if (userEntity == null) {
            return createUser(chatId);
        }
        return userEntity;
    }

    @SneakyThrows
    public UserEntity findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User not found")
        );
    }

    public UserEntity updateUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @SneakyThrows
    @Transactional
    public UserEntity updateUserByChatId(String chatId, UserDto userDto) {
        UserEntity userEntity = getUserByTelegramChatId(chatId);
        if (userEntity == null) {
            throw new NotFoundException("User not found");
        }
        fillEntityCommon(userEntity, userDto);
        return userRepository.save(userEntity);
    }

    @SneakyThrows
    @Transactional
    public UserEntity updateUserById(UUID id, UserDto userDto) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        fillEntityCommon(userEntity, userDto);

        return userRepository.save(userEntity);
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public UserEntity updateUserRoleAndPassword(UUID id, RoleEnum role, String password) {
        UserEntity user = findById(id);
        user.setRole(role);
        user.setPassword(password);
        return userRepository.save(user);
    }

    private void fillEntityCommon(UserEntity userEntity, UserDto userDto) {
        String surveyStatus = userDto.getSurveyStatus();
        if (surveyStatus != null) {
            userEntity.setSurveyStatus(surveyStatus);
        }

        Integer currentQuestionIndex = userDto.getCurrentQuestionIndex();
        if (currentQuestionIndex != null) {
            userEntity.setCurrentQuestionIndex(currentQuestionIndex);
        }

        Integer age = userDto.getAge();
        if (age != null) {
            userEntity.setAge(age);
        }

        Integer fat = userDto.getFat();
        if (fat != null) {
            userEntity.setFat(fat);
        }

        String water = userDto.getWater();
        if (water != null) {
            userEntity.setWater(water);
        }

        Integer height = userDto.getHeight();
        if (height != null) {
            userEntity.setHeight(height);
        }

        Integer weight = userDto.getWeight();
        if (weight != null) {
            userEntity.setWeight(weight);
        }

        Integer desiredWeight = userDto.getDesiredWeight();
        if (desiredWeight != null) {
            userEntity.setDesiredWeight(desiredWeight);
        }

        // Цели и предпочтения
        String aim = userDto.getAim();
        if (aim != null) {
            userEntity.setAim(aim);
        }

        String bodyType = userDto.getBodyType();
        if (bodyType != null) {
            userEntity.setBodyType(bodyType);
        }

        String desiredBody = userDto.getDesiredBody();
        if (desiredBody != null) {
            userEntity.setDesiredBody(desiredBody);
        }

        String workBody = userDto.getWorkBody();
        if (workBody != null) {
            userEntity.setWorkBody(workBody);
        }

        String mealPlan = userDto.getMealPlan();
        if (mealPlan != null) {
            userEntity.setMealPlan(mealPlan);
        }

        String junkFood = userDto.getJunkFood();
        if (junkFood != null) {
            userEntity.setJunkFood(junkFood);
        }

        // Тренировки
        Integer trainingLevel = userDto.getTrainingLevel();
        if (trainingLevel != null) {
            userEntity.setTrainingLevel(trainingLevel);
        }

        String pressUp = userDto.getPressUp();
        if (pressUp != null) {
            userEntity.setPressUp(pressUp);
        }

        String lifting = userDto.getLifting();
        if (lifting != null) {
            userEntity.setLifting(lifting);
        }

        String trainingFrequency = userDto.getTrainingFrequency();
        if (trainingFrequency != null) {
            userEntity.setTrainingFrequency(trainingFrequency);
        }

        Integer time = userDto.getTime();
        if (time != null) {
            userEntity.setTime(time);
        }

        String preferredSport = userDto.getPreferredSport();
        if (preferredSport != null) {
            userEntity.setPreferredSport(preferredSport);
        }

        String additionalAim = userDto.getAdditionalAim();
        if (additionalAim != null) {
            userEntity.setAdditionalAim(additionalAim);
        }

        String preferredTrainingSpace = userDto.getPreferredTrainingSpace();
        if (preferredTrainingSpace != null) {
            userEntity.setPreferredTrainingSpace(preferredTrainingSpace);
        }

        String inventory = userDto.getInventory();
        if (inventory != null) {
            userEntity.setInventory(inventory);
        }

        UUID workoutPlanId = userDto.getWorkoutPlanId();
        if (workoutPlanId != null) {
            userEntity.setWorkoutPlanId(workoutPlanId);
        }

        UUID nutritionPlanId = userDto.getNutritionPlanId();
        if (nutritionPlanId != null) {
            userEntity.setNutritionPlanId(nutritionPlanId);
        }
    }
}
