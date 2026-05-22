package com.rvinproject.camerarentalbe.app.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

public class PageableUtil {
    private PageableUtil() {
    }

    public static Pageable from(int page, int size, Map<String, String[]> params) {
        Sort.Direction direction = "asc".equalsIgnoreCase(QueryUtil.direction(params)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(Math.max(page, 0), Math.max(1, Math.min(size, 100)), Sort.by(direction, QueryUtil.sortField(params)));
    }
}
