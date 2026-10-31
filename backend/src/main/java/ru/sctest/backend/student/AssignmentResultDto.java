package ru.sctest.backend.student;

import java.util.UUID;
import java.time.LocalDateTime;

import ru.sctest.backend.db.model.AssignmentResultStatus;
import ru.sctest.backend.db.model.AssignmentResult;

record AssignmentResultDto(
    AssignmentDto assignment, 
    AssignmentResultStatus status,
    Boolean isGraded,
    LocalDateTime startedAt
) {
    public AssignmentResultDto(AssignmentResult assignmentResult) {
        var assignment = assignmentResult.getAssignment();
        var assignmentDto = new AssignmentDto(
            assignment.getId(), 
            assignment.getTitle()
        );
        this(
            assignmentDto,
            assignmentResult.getStatus(),
            assignmentResult.getIsGraded(),
            assignmentResult.getStartedAt()
        );
    }
}

record AssignmentDto(UUID id, String title) {}