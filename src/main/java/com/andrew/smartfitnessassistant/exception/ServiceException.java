package com.andrew.smartfitnessassistant.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends RuntimeException {
    private String message;
}
