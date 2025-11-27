package com.andrew.smartfitnessassistant.entity;

import com.andrew.smartfitnessassistant.common.RemindRepeatEnum;
import com.andrew.smartfitnessassistant.common.RemindTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Accessors(chain = true)
@Getter
@Setter
@Entity
@Table(name = "reminds")
public class RemindEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID userId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "remind_type_enum")
    private RemindTypeEnum type;

    private String message;

    private LocalTime remindTime;

    private RemindRepeatEnum repeat;

    private LocalDate stopRepeat;
}
