package ru.sctest.backend.teacher.students.student.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import ru.sctest.backend.db.model.AssignmentResultStatus;

public record AssignmentDto(
    Long id,
    UUID assignmentId,
    AssignmentResultStatus status,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    String assignmentTitle,
    Integer slideCount,
    Long correctCount,
    Long incorrectCount
)
{

}
