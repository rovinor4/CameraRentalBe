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
        data.put("total_elements", page.getTotalElements());
        data.put("total_pages", page.getTotalPages());
        data.put("last", page.isLast());
        return data;
    }
}
