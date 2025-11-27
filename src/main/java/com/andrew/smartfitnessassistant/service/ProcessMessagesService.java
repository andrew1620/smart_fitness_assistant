package com.andrew.smartfitnessassistant.service;

import com.andrew.smartfitnessassistant.common.UserSurvayStatusEnum;
import com.andrew.smartfitnessassistant.dto.RemindDto;
import com.andrew.smartfitnessassistant.dto.UserDto;
import com.andrew.smartfitnessassistant.entity.AnswerEntity;
import com.andrew.smartfitnessassistant.entity.QuestionEntity;
import com.andrew.smartfitnessassistant.entity.RemindEntity;
import com.andrew.smartfitnessassistant.entity.UserEntity;
import com.andrew.smartfitnessassistant.entity.WorkoutPlanEntity;
import com.andrew.smartfitnessassistant.mapper.UserMapper;
import com.andrew.smartfitnessassistant.repository.QuestionRepository;
import com.andrew.smartfitnessassistant.service.event.TelegramEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessMessagesService {

    private final QuestionRepository questionRepository;
    private final MessageOutputService messageOutputService;
    private final UserService userService;
    private final RecommendationService recommendationService;
    private final TelegramEventPublisher telegramEventPublisher;
    private final RemindService remindService;
    private final OutboxEventService outboxEventService;
    private final WorkoutPlanService workoutPlanService;
    private final AuthenticateService adminService;

    public void processStart(UserEntity user, Update update) {
        var chatId = String.valueOf(update.getMessage().getChatId());
        var username = update.getMessage().getFrom().getUserName();

        UserDto userDto = UserDto.builder().surveyStatus(UserSurvayStatusEnum.AWAITING_START.toString())
                .currentQuestionIndex(0).build();
        userService.updateUserByChatId(user.getTelegramChatId(), userDto);

        String startMessage = messageOutputService.startWelcomeMessage(username);
        telegramEventPublisher.sendMessageWithKeyboard(chatId,startMessage, List.of("Да", "Нет"));
    }

    public void processDefault(UserEntity user, String message) {
        if ((message.equals("Да") && user.getSurveyStatus().equals(UserSurvayStatusEnum.AWAITING_START.toString()))
                || user.getSurveyStatus().equals(UserSurvayStatusEnum.IN_PROGRESS.toString())) {
            user.setSurveyStatus(UserSurvayStatusEnum.IN_PROGRESS.toString());
            initSurvey(user, message);
        } else {
            telegramEventPublisher.sendMessage(user.getTelegramChatId(), messageOutputService.unknownCommandMessage());
        }
    }

    @SneakyThrows
    private void completeSurvey(UserEntity user) {

        UserDto userDto = UserDto.builder().surveyStatus(UserSurvayStatusEnum.COMPLETED.toString())
                .currentQuestionIndex(0).build();
        userService.updateUserById(user.getId(), userDto);
        telegramEventPublisher.sendMessage(user.getTelegramChatId(), messageOutputService.surveyFinishedMessage());
        Thread.sleep(3000);
        recommendationService.getRecommendationPlan(user);
    }


    public void initSurvey(UserEntity userEntity, String message) {
        QuestionEntity previousQuestion = questionRepository.findByPosition(userEntity.getCurrentQuestionIndex() - 1);
        QuestionEntity question = questionRepository.findByPosition(userEntity.getCurrentQuestionIndex());
        UserDto userDto = UserDto.builder()
                .surveyStatus(UserSurvayStatusEnum.IN_PROGRESS.toString())
                .currentQuestionIndex(userEntity.getCurrentQuestionIndex() + 1)
                .build();
        if (!message.equals("Да")) {
            UserMapper.updateUserDtoFromQuestion(previousQuestion, message, userDto);
        }

        UserEntity updatedUser = userService.updateUserById(userEntity.getId(), userDto);

        if (question == null) {
            completeSurvey(updatedUser);
        } else {
            telegramEventPublisher.sendMessageWithKeyboard(userEntity.getTelegramChatId(),
                    question.getTitle(), question.getAnswers().stream().map(AnswerEntity::getTitle).toList());
        }

    }
    public void processSetRemindHelp(UserEntity user) {
        WorkoutPlanEntity plan = workoutPlanService.findById(user.getWorkoutPlanId());
        String message = messageOutputService.remindWorkoutHelpMessage(plan.getWorkouts());
        telegramEventPublisher.sendMessage(user.getTelegramChatId(), message);
        telegramEventPublisher.sendMessage(user.getTelegramChatId(), "/setRemind 18:30:00, 1, 2026-12-31");
    }
    public void processSetRemind(UserEntity userEntity, String message) {
        RemindEntity remindEntity = remindService.setRemind(userEntity, message);
        RemindDto remindDto = new RemindDto().setUserId(userEntity.getId());
        outboxEventService.createRemindOutboxEvent(remindDto);
        String resultMessage = messageOutputService.remindWorkoutSetMessage(remindEntity.getRemindTime());
        telegramEventPublisher.sendMessage(userEntity.getTelegramChatId(), resultMessage);
    }
    public void processHelp(UserEntity user) {
        telegramEventPublisher.sendMessage(user.getTelegramChatId(), messageOutputService.availableCommandsMessage());
    }
    public void processAdminLogin(UserEntity user, String message) {
        String password = message.substring("/login".length()).trim();

        if (password.isEmpty()) {
            telegramEventPublisher.sendMessage(user.getTelegramChatId(),
                    "❌ Неверный формат команды. Используйте: /login <пароль>");
            return;
        }

        if (adminService.authenticateAdmin(user, password)) {
            telegramEventPublisher.sendMessage(user.getTelegramChatId(),
                    "✅ Вы успешно авторизовались как администратор!\n\n" +
                            "Доступные команды:\n" +
                            "• /admin_stats - Статистика бота\n" +
                            "• /admin_users - Список пользователей\n" +
                            "• /admin_logout - Выйти");
        } else {
            telegramEventPublisher.sendMessage(user.getTelegramChatId(),
                    "❌ Неверный пароль администратора");
        }
    }

    public void processAdminLogout(UserEntity user) {
        adminService.logoutAdmin(user.getTelegramChatId());
        telegramEventPublisher.sendMessage(user.getTelegramChatId(),
                "👋 Вы вышли из режима администратора");
    }
}
