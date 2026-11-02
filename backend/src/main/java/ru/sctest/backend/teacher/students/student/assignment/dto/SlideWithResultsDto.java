package ru.sctest.backend.teacher.students.student.assignment.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record SlideWithResultsDto(
    Long id,
    String type,
    Integer position,
    Map<String, Object> content,
    Object studentAnswer,
    Boolean isCorrect,
    LocalDateTime answeredAt
) {}
