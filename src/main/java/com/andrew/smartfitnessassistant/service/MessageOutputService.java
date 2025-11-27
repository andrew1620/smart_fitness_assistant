package com.andrew.smartfitnessassistant.service;

import com.andrew.smartfitnessassistant.entity.NutritionPlanEntity;
import com.andrew.smartfitnessassistant.entity.UserEntity;
import com.andrew.smartfitnessassistant.entity.WorkoutEntity;
import com.andrew.smartfitnessassistant.entity.WorkoutPlanEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalTime;
import java.util.List;

@Service
public class MessageOutputService {
    public String startWelcomeMessage(String username) {
        String text = """
                🏋️‍♂️ Добро пожаловать в Smart Fitness Assistant, %s!
                               \s
                                Я помогу тебе создать персонализированную программу тренировок и питания.
                               \s
                                Для этого мне нужно задать несколько вопросов о твоих целях и предпочтениях.
                               \s
                                💡 Это займет всего 2-3 минуты!
                               \s
                                Готов ли ты начать опрос?
                """;
        String formattedText = text.formatted(username);
        return formattedText;
    }

    public String surveyFinishedMessage() {
        return """
                Опрос окончен!
                Рассчитываю программу тренировки...
                """;
    }

    public String planCreatedMessage(WorkoutPlanEntity workoutPlan, NutritionPlanEntity nutritionPlan) {

        return String.format("""
                🎯 ВАШ ПЕРСОНАЛЬНЫЙ ПЛАН
                
                %s
                
                %s
                
                💪 Начинайте следовать плану и отслеживайте прогресс!
                """, planWorkoutMessage(workoutPlan), planNutritionMessage(nutritionPlan));
    }

    public String planWorkoutMessage(WorkoutPlanEntity workoutPlan) {
        StringBuilder message = new StringBuilder();
        message.append(workoutPlan.getName() + "\n\n");
        workoutPlan.getWorkouts().forEach(workout -> message.append(workout.getDescription() + "\n\n"));
        return message.toString();
    }

    public String planNutritionMessage(NutritionPlanEntity nutritionPlan) {
        return String.format("""
            🍽️ ВАШ ПЛАН ПИТАНИЯ
            
            📊 Суточная норма:
            • Калории: %d ккал
            • Белки: %d г
            • Жиры: %d г
            • Углеводы: %d г
            
            🥗 Пример рациона на день:
            🍳 Завтрак: Овсянка + яйца + овощи
            🍎 Перекус: Творог + фрукты
            🍗 Обед: Гречка + куриная грудка + салат
            🥛 Перекус: Протеин + орехи
            🐟 Ужин: Рыба + овощи на пару
            
            💡 Рекомендации:
            • Пейте 2-3 литра воды в день
            • Ешьте каждые 3-4 часа
            • Следите за количеством белка
            """, nutritionPlan.getCalories(), nutritionPlan.getProtein(),nutritionPlan.getFat(), nutritionPlan.getCarbs());
    }

    public String remindWorkoutMessage(String workoutDescription) {
        return String.format("""
        🏋️‍♂️ *ПОРА НА ТРЕНИРОВКУ!* 🏋️‍♂️

        ⏰ *Время выполнить план:*

        %s

        💪 *Мотивация:* 
        Каждое повторение приближает тебя к цели!
        Ты становишься сильнее с каждой тренировкой!

        🔥 *Совет:* 
        Не пропускай разминку и заминку!
        Пей воду во время тренировки!

        📊 *Отслеживай прогресс в приложении*

        #тренировка #спорт #здоровье
        """, workoutDescription);
    }

    public String remindWorkoutSetMessage(LocalTime time) {
        return String.format("""
        🔥 *Напоминание установлено* 
        Время: %s

        #тренировка #спорт #здоровье
        """, time.toString());
    }
    public String remindWorkoutHelpMessage(List<WorkoutEntity> workouts) {
        StringBuilder message = new StringBuilder();

        message.append("""
        ⏰ *Установка напоминания*
        
        *Формат:* `/setRemind время, номер, дата_окончания`
        
        *Пример:* `/setRemind 18:30:00, 1, 2026-12-31`
        """);

        if (workouts != null && !workouts.isEmpty()) {
            message.append("\n*Ваши тренировки:*\n");

            for (int i = 0; i < workouts.size(); i++) {
                String cleanDesc = workouts.get(i).getDescription()
                        .replaceAll("\\s+", " ")
                        .replaceAll("•", "➜")
                        .trim();

                if (cleanDesc.length() > 80) {
                    cleanDesc = cleanDesc.substring(0, 80) + "...";
                }

                message.append(String.format(
                        "`%d` %s\n",
                        i + 1,
                        cleanDesc
                ));
            }
        }

        message.append("""
        \n*Копировать сообщение ниже:*
        """);

        return message.toString();
    }

    public String unknownCommandMessage() {
        return """
        🤔 *Неизвестная команда*

        Кажется, я не понимаю, что вы имеете в виду.

       %s

        🏃‍♂️ *Давайте продолжим ваше fitness-путешествие!*
        """.formatted(availableCommandsMessage());
    }

    public String availableCommandsMessage() {
        return """
        📋 *Доступные команды:*
        /start - Начать работу с ботом
        /setRemind - Установить напоминание о тренировке
        /setRemindHelp - Помощь по команде установки напоминания
        /login <password> - Войти как администратор
        /admin_logout - Выйти из роли администратора
        /admin_promote <chatId> <пароль> - Дать права админа пользователю
        /help - Получить справку по командам
        """;
    }

    public String userListMessage(List<UserEntity> users) {
        StringBuilder message = new StringBuilder("👥 *Список пользователей:*\n\n");

        users.forEach(userEntity -> {
            message
                    .append("• ID: " + userEntity.getId() + "\n")
                    .append("• ChatID: " + userEntity.getTelegramChatId() + "\n\n");
        });
        return message.toString();
    }

    public String userPromotedToAdminMessage(String userId) {
        return "✅ Пользователь " + userId + " назначен администратором";
    }

    public String youPromotedToAdminMessage(String password) {
        return "🎉 Вы назначены администратором!\n\n" +
                "Для входа используйте:\n" +
                "`/login " + password + "`\n\n";
    }

    public String wrongCommandFormatMessage() {
        return "❌ Неверный формат команды. Используйте: /login <пароль>";
    }

    public String successAuthenticationMessage() {
        return "✅ Вы успешно авторизовались как администратор!\n\n" +
                "Доступные команды:\n" +
                "• /admin_stats - Статистика бота\n" +
                "• /admin_users - Список пользователей\n" +
                "• /admin_logout - Выйти";
    }

    public String wrongAdminPasswordMessage() {
        return "❌ Неверный пароль администратора";
    }

    public String exitAdminMessage() {
        return "👋 Вы вышли из режима администратора";
    }

    public ReplyKeyboardMarkup getKeyboardForList(List<String> options) {
        var keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> rows = options.stream().map(option -> {
            KeyboardRow row = new KeyboardRow();
            row.add(option);
            return row;
        }).toList();

        keyboard.setKeyboard(rows);
        return keyboard;
    }

}
