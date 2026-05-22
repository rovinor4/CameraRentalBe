package com.rvinproject.camerarentalbe.app.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

public class ValidationUtil {
    private ValidationUtil() {
    }

    public static void oneOf(String value, Set<String> allowed, String field) {
        if (value == null || !allowed.contains(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " tidak valid");
        }
    }

    public static <E extends Enum<E>> E enumValue(String value, Class<E> enumClass, String field) {
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " tidak valid");
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " tidak valid");
        }
    }

    public static void validId(Long id, String field) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " tidak valid");
        }
    }
}
