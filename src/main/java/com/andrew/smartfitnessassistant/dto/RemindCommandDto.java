package com.andrew.smartfitnessassistant.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalTime;

@Accessors(chain = true)
@Getter
@Setter
public class RemindCommandDto {
    private LocalTime remindTime;
    private Integer workoutIndex;
    private LocalDate stopRepeat;
}
