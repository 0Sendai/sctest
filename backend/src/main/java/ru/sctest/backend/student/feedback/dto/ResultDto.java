package ru.sctest.backend.student.feedback.dto;

import java.time.LocalDateTime;

import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.AssignmentResultStatus;

public record ResultDto(
    AssignmentResultStatus status, 
    LocalDateTime startedAt,
    LocalDateTime completedAt
) 
{
    public ResultDto(AssignmentResult assignmentResult) {
        this(
            assignmentResult.getStatus(),
            assignmentResult.getStartedAt(),
            assignmentResult.getCompletedAt()
        );
    }
}
