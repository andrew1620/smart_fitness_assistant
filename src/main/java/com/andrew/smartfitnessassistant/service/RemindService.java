package com.andrew.smartfitnessassistant.service;

import com.andrew.smartfitnessassistant.common.RemindTypeEnum;
import com.andrew.smartfitnessassistant.dto.RemindCommandDto;
import com.andrew.smartfitnessassistant.dto.RemindDto;
import com.andrew.smartfitnessassistant.entity.RemindEntity;
import com.andrew.smartfitnessassistant.entity.UserEntity;
import com.andrew.smartfitnessassistant.entity.WorkoutPlanEntity;
import com.andrew.smartfitnessassistant.repository.RemindRepository;
import com.andrew.smartfitnessassistant.service.event.TelegramEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemindService {
    private final RemindRepository remindRepository;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final TelegramEventPublisher telegramEventPublisher;
    private final MessageOutputService messageOutputService;
    private final WorkoutPlanService workoutPlanService;

    public void processRemindEvent(String payload) {
        RemindDto remindDto = payloadToRemindDto(payload);
        UserEntity user = userService.findById(remindDto.getUserId());
        List<RemindEntity> reminds = remindRepository.findAllByUserId(user.getId());
        if (reminds.isEmpty()) {
            return;
        }
        reminds.forEach(remind -> processRemind(remind, user));
    }

    private void processRemind(RemindEntity remind, UserEntity user) {
        LocalTime currentTime = LocalTime.now();
        LocalTime remindTime = remind.getRemindTime();
        if (shouldDeleteRemind(remind.getStopRepeat())) {
            deleteRemind(remind.getId());
        }
        if (isTimeWithinSeconds(currentTime, remindTime, 20)) {
            telegramEventPublisher.sendMessage(user.getTelegramChatId(), remind.getMessage());
        }
    }

    private boolean isTimeWithinSeconds(LocalTime currentTime, LocalTime remindTime, Integer seconds) {
        Duration duration = Duration.between(currentTime, remindTime).abs();
        return duration.toSeconds() <= seconds;
    }

    private boolean shouldDeleteRemind(LocalDate stopRepeat) {
        if (stopRepeat == null) {
            return false;
        }

        return LocalDate.now().isAfter(stopRepeat) || LocalDate.now().isEqual(stopRepeat);
    }

    public boolean shouldCompleteRemindOutboxEvent(String payload) {
        RemindDto remindDto = payloadToRemindDto(payload);
        UserEntity user = userService.findById(remindDto.getUserId());
        List<RemindEntity> reminds = remindRepository.findAllByUserId(user.getId());
        return reminds.isEmpty();
    }

    private RemindDto payloadToRemindDto(String payload) {
        try {
            return objectMapper.readValue(payload, RemindDto.class);
        } catch (Exception e) {
            log.error("Ошибка при десериализации RemindDto: {}", e.getMessage());
        }
        return null;
    }
    public RemindEntity setRemind(UserEntity user, String message) {
        RemindCommandDto remindCommandDto = parseRemindCommand(message);
        WorkoutPlanEntity plan = workoutPlanService.findById(user.getWorkoutPlanId());
        int workoutsCount = plan.getWorkouts().size();
        Integer workoutIndex = remindCommandDto.getWorkoutIndex();
        if (workoutIndex < 0 || workoutIndex > workoutsCount || workoutsCount == 0) {
            throw new IllegalArgumentException("Bad workout index");
        }
        RemindEntity remindEntity = new RemindEntity().setUserId(user.getId()).setType(RemindTypeEnum.TRAINING)
                .setRemindTime(remindCommandDto.getRemindTime())
                .setStopRepeat(remindCommandDto.getStopRepeat())
                .setMessage(messageOutputService.remindWorkoutMessage(plan.getWorkouts().get(workoutIndex - 1)
                        .getDescription()));

        return remindRepository.save(remindEntity);
    }
    public RemindCommandDto parseRemindCommand(String fullCommand) {
        try {
            if (!fullCommand.startsWith("/setRemind")) {
                throw new IllegalArgumentException("Команда должна начинаться с /setRemind");
            }

            String arguments = fullCommand.substring("/setRemind".length()).trim();

            return parseRemindArguments(arguments);

        } catch (Exception e) {
            log.error("Ошибка парсинга команды: {}", fullCommand, e);
            throw new IllegalArgumentException("Неверный формат команды: " + e.getMessage(), e);
        }
    }
    public RemindCommandDto parseRemindArguments(String command) {
        var timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            String[] parts = command.split("\\s*,\\s*");

            if (parts.length != 3) {
                throw new IllegalArgumentException("Неверный формат команды. Ожидается: время, индекс, дата_остановки");
            }

            String timeStr = parts[0].trim();
            String indexStr = parts[1].trim();
            String dateStr = parts[2].trim();

            LocalTime remindTime = LocalTime.parse(timeStr, timeFormatter);

            Integer workoutIndex = Integer.parseInt(indexStr);
            if (workoutIndex < 0) {
                throw new IllegalArgumentException("Индекс тренировки не может быть отрицательным");
            }

            LocalDate stopRepeat = LocalDate.parse(dateStr, dateTimeFormatter);

            if (stopRepeat.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Дата остановки не может быть в прошлом");
            }

            RemindCommandDto result = new RemindCommandDto();
            result.setRemindTime(remindTime);
            result.setWorkoutIndex(workoutIndex);
            result.setStopRepeat(stopRepeat);

            return result;

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ошибка парсинга даты или времени: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Индекс тренировки должен быть числом");
        }
    }
    private void deleteRemind(UUID remindId) {
        remindRepository.deleteById(remindId);
    }
}
