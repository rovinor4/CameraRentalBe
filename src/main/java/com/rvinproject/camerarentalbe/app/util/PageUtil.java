package com.rvinproject.camerarentalbe.app.util;

import org.springframework.data.domain.Page;

import java.util.LinkedHashMap;
import java.util.Map;

public class PageUtil {
    private PageUtil() {
    }

    public static Map<String, Object> response(Page<?> page) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", page.getContent());
        data.put("page", page.getNumber());
        data.put("size", page.getSize());
        data.put("totalElements", page.getTotalElements());
        data.put("totalPages", page.getTotalPages());
        data.put("last", page.isLast());
        return data;
    }
}
