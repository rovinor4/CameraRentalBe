package com.rvinproject.camerarentalbe.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JSONFormat<T> {
    private T data;
    private Object errors;
    private String message;

    public static <T> JSONFormat<T> success(T data, String message) {
        return new JSONFormat<>(data, null, message);
    }

    public static JSONFormat<Object> error(Object errors, String message) {
        return new JSONFormat<>(null, errors, message);
    }
}