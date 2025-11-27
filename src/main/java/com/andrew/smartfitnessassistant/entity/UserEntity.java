package com.andrew.smartfitnessassistant.entity;

import com.andrew.smartfitnessassistant.common.RoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String telegramChatId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "user_role_enum")
    private RoleEnum role;

    private String password;

    private UUID workoutPlanId;

    private UUID nutritionPlanId;

    private Integer currentQuestionIndex;

    private String surveyStatus;

    private Integer age;
    private String aim;
    private String bodyType;
    private Integer fat;
    private String water;
    private Integer height;
    private Integer weight;
    private Integer desiredWeight;
    private String desiredBody;
    private String workBody;
    private String mealPlan;
    private String junkFood;
    private Integer trainingLevel;
    private String pressUp;
    private String lifting;
    private String trainingFrequency;
    private Integer time;
    private String preferredSport;
    private String additionalAim;
    private String preferredTrainingSpace;
    private String inventory;
}
