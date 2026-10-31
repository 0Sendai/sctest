package ru.sctest.backend.teacher.assignments.edit.dto;

import java.util.Map;

import ru.sctest.backend.db.model.Slide;

public record SlideDto(String type, Map<String, Object> content) {
    public SlideDto(Slide slide) {
        this(slide.getType(), slide.getContent());
    }
}