package ru.sctest.backend.teacher.assignments.create;

import java.util.Map;
import java.util.List;

public record AssignmentDto(
    String title,
    List<Map<String,Object>> slides,
    List<Long> studentIds
) {}
