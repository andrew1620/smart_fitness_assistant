package com.andrew.smartfitnessassistant.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Accessors(chain = true)
@Getter
@Setter
public class RemindDto {
    private UUID userId;
}
