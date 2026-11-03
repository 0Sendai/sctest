package ru.sctest.backend.student.assignment.dto;

import java.util.Map;

public record SlideDto(
    Long id,
    String type,
    Integer position,
    Map<String, Object> content
) {}
