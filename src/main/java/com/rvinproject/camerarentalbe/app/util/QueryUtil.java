package com.rvinproject.camerarentalbe.app.util;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class QueryUtil {
    private static final Set<String> RESERVED_PARAMS = Set.of("page", "size", "sort", "direction", "search", "startDate", "endDate", "dateField", "start_date", "end_date", "date_field");

    private QueryUtil() {
    }

    public static <T> Specification<T> specification(Map<String, String[]> params, List<String> searchFields, String defaultDateField) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            String search = first(params, "search");
            if (search != null && !search.isBlank()) {
                String keyword = "%" + search.toLowerCase(Locale.ROOT) + "%";
                List<Predicate> searchPredicates = new ArrayList<>();
                for (String field : searchFields) {
                    searchPredicates.add(builder.like(builder.lower(path(root, field).as(String.class)), keyword));
                }
                predicates.add(builder.or(searchPredicates.toArray(new Predicate[0])));
            }

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String key = propertyName(entry.getKey());
                String value = first(entry.getValue());
                if (RESERVED_PARAMS.contains(entry.getKey()) || value == null || value.isBlank()) {
                    continue;
                }
                predicates.add(builder.like(builder.lower(path(root, key).as(String.class)), "%" + value.toLowerCase(Locale.ROOT) + "%"));
            }

            String dateField = first(params, "date_field", "dateField");
            if (dateField == null || dateField.isBlank()) {
                dateField = defaultDateField;
            }
            String startDate = first(params, "start_date", "startDate");
            String endDate = first(params, "end_date", "endDate");
            if (dateField != null && !dateField.isBlank() && startDate != null && !startDate.isBlank()) {
                predicates.add(builder.greaterThanOrEqualTo(path(root, dateField).as(LocalDate.class), LocalDate.parse(startDate)));
            }
            if (dateField != null && !dateField.isBlank() && endDate != null && !endDate.isBlank()) {
                predicates.add(builder.lessThanOrEqualTo(path(root, dateField).as(LocalDate.class), LocalDate.parse(endDate)));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static <T> Specification<T> dateTimeSpecification(Map<String, String[]> params, List<String> searchFields, String defaultDateField) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            String search = first(params, "search");
            if (search != null && !search.isBlank()) {
                String keyword = "%" + search.toLowerCase(Locale.ROOT) + "%";
                List<Predicate> searchPredicates = new ArrayList<>();
                for (String field : searchFields) {
                    searchPredicates.add(builder.like(builder.lower(path(root, field).as(String.class)), keyword));
                }
                predicates.add(builder.or(searchPredicates.toArray(new Predicate[0])));
            }

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String key = propertyName(entry.getKey());
                String value = first(entry.getValue());
                if (RESERVED_PARAMS.contains(entry.getKey()) || value == null || value.isBlank()) {
                    continue;
                }
                predicates.add(builder.like(builder.lower(path(root, key).as(String.class)), "%" + value.toLowerCase(Locale.ROOT) + "%"));
            }

            String dateField = first(params, "date_field", "dateField");
            if (dateField == null || dateField.isBlank()) {
                dateField = defaultDateField;
            }
            String startDate = first(params, "start_date", "startDate");
            String endDate = first(params, "end_date", "endDate");
            if (dateField != null && !dateField.isBlank() && startDate != null && !startDate.isBlank()) {
                predicates.add(builder.greaterThanOrEqualTo(path(root, dateField).as(LocalDateTime.class), LocalDate.parse(startDate).atStartOfDay()));
            }
            if (dateField != null && !dateField.isBlank() && endDate != null && !endDate.isBlank()) {
                predicates.add(builder.lessThanOrEqualTo(path(root, dateField).as(LocalDateTime.class), LocalDate.parse(endDate).atTime(LocalTime.MAX)));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static String sortField(Map<String, String[]> params) {
        String sort = first(params, "sort");
        return sort == null || sort.isBlank() ? "id" : propertyName(sort);
    }

    public static String direction(Map<String, String[]> params) {
        String direction = first(params, "direction");
        return direction == null || direction.isBlank() ? "desc" : direction;
    }

    private static String first(Map<String, String[]> params, String key) {
        return first(params.get(key));
    }

    private static String first(Map<String, String[]> params, String primaryKey, String fallbackKey) {
        String value = first(params, primaryKey);
        return value == null ? first(params, fallbackKey) : value;
    }

    private static String first(String[] values) {
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    private static Path<?> path(Path<?> root, String field) {
        Path<?> current = root;
        for (String part : field.split("\\.")) {
            current = current.get(part);
        }
        return current;
    }

    private static String propertyName(String value) {
        if (value == null || !value.contains("_")) {
            return value;
        }
        StringBuilder result = new StringBuilder();
        boolean uppercaseNext = false;
        for (char character : value.toCharArray()) {
            if (character == '_') {
                uppercaseNext = true;
                continue;
            }
            result.append(uppercaseNext ? Character.toUpperCase(character) : character);
            uppercaseNext = false;
        }
        return result.toString();
    }
}
