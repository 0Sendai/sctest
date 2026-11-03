package ru.sctest.backend.student.assignment.dto;

import java.util.UUID;

import ru.sctest.backend.db.model.Assignment;

public record AssignmentDto(UUID id, String title) {
    public AssignmentDto(Assignment assignment) {
        this(assignment.getId(), assignment.getTitle());
    }
}
