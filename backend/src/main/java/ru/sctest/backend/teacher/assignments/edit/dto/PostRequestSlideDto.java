package ru.sctest.backend.teacher.assignments.edit.dto;

import java.util.List;

public record PostRequestSlideDto(
    String type,
    String question,
    List<String> variants,
    Integer correctVariantIndex
) {}
