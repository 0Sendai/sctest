package ru.sctest.backend.teacher.students.student.assignment.dto;

import java.time.LocalDateTime;

import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.AssignmentResultStatus;

public record ResultDto(
    Long id,
    AssignmentResultStatus status,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    Long currentSlideId
)
{
    public ResultDto(AssignmentResult assignmentResult) {
        this(
            assignmentResult.getId(),
            assignmentResult.getStatus(),
            assignmentResult.getStartedAt(),
            assignmentResult.getCompletedAt(),
            assignmentResult.getCurrentSlide().getId()
        );
    }
}
