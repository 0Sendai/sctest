package ru.sctest.backend.student.assignment.dto;

import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.AssignmentResultStatus;

public record AssignmentResultDto(
    Long id,
    Long currentSlideId,
    AssignmentResultStatus status
)
{
    public AssignmentResultDto(AssignmentResult assignmentResult) {
        this(
            assignmentResult.getId(), 
            assignmentResult.getCurrentSlide().getId(),
            assignmentResult.getStatus()
        );
    }
}
