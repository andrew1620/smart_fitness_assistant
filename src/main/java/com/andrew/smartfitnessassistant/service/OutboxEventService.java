package com.andrew.smartfitnessassistant.service;

import com.andrew.smartfitnessassistant.common.OutboxEventStatusEnum;
import com.andrew.smartfitnessassistant.common.OutboxEventTypeEnum;
import com.andrew.smartfitnessassistant.dto.RemindDto;
import com.andrew.smartfitnessassistant.entity.OutboxEventEntity;
import com.andrew.smartfitnessassistant.repository.OutboxEventRepository;
import com.andrew.smartfitnessassistant.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventService {
    private final OutboxEventRepository outboxEventRepository;
    private final RemindService remindService;
    private final AuthenticateService authenticateService;

    @Scheduled(cron = "${outbox-process-cron-time}")
    public void processOutboxEvents() {
        log.info("Processing outbox events");
        outboxEventRepository.findAllByStatus(OutboxEventStatusEnum.NEW).forEach(
                event -> {
                    switch (event.getType()) {
                        case REMIND -> {
                            handleRemindEvent(event);
                        }
                        case CLEAN_EXPIRED_SESSIONS -> {
                            handleCleanExpiredSessionsEvent(event);
                        }
                        default -> {
                            log.error("Присутствует ивент с неизвестным типом: {}", event.getId());
                        }
                    }

                }
        );
    }

    private void handleEvent(OutboxEventEntity event, Consumer<String> executor, Function<String,
            Boolean> shouldCompleteEvent) {
        try {
            log.info("Событие {} взято в обработку", event.getType());
            event.setStatus(OutboxEventStatusEnum.IN_PROCESS);
            outboxEventRepository.save(event);
            executor.accept(event.getPayload());
            boolean shouldComplete = shouldCompleteEvent.apply(event.getPayload());
            if (shouldComplete) {
                event.setStatus(OutboxEventStatusEnum.COMPLETED);
            } else {
                event.setStatus(OutboxEventStatusEnum.NEW);
            }
            outboxEventRepository.save(event);
            log.info("Событие {} выполнено успешно", event.getType());
        } catch (Exception e) {
            log.error("Ошибка во время выполнения ивента: {}", e.getMessage());
        }
    }

    private void handleRemindEvent(OutboxEventEntity event) {
        handleEvent(event,
                remindService::processRemindEvent,
                remindService::shouldCompleteRemindOutboxEvent);
    }

    private void handleCleanExpiredSessionsEvent(OutboxEventEntity event) {
        handleEvent(event,
                (payload) -> authenticateService.cleanupExpiredSessions(),
                (payload) -> false);
    }

    public OutboxEventEntity createOutboxEvent(String payload, OutboxEventTypeEnum type) {
        OutboxEventEntity outboxEventEntity = new OutboxEventEntity()
                .setStatus(OutboxEventStatusEnum.NEW).setType(type).setPayload(payload);
        return outboxEventRepository.save(outboxEventEntity);
    }

    public OutboxEventEntity createRemindOutboxEvent(RemindDto remindDto) {
        String payload = JsonUtils.toJson(remindDto);
        return createOutboxEvent(payload, OutboxEventTypeEnum.REMIND);
    }
}
