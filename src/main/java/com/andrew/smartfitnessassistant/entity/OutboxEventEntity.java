package com.andrew.smartfitnessassistant.entity;

import com.andrew.smartfitnessassistant.common.OutboxEventStatusEnum;
import com.andrew.smartfitnessassistant.common.OutboxEventTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Accessors(chain = true)
@Getter
@Setter
@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "outbox_event_type_enum")
    private OutboxEventTypeEnum type;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "outbox_event_status_enum")
    private OutboxEventStatusEnum status;

    private String payload;
}
