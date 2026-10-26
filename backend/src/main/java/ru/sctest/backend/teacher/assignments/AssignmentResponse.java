package ru.sctest.backend.teacher.assignments;

import java.util.List;

public sealed interface AssignmentResponse 
    permits AssignmentResponse.Success, AssignmentResponse.Failure {
        record Success(List<AssignmentDto> assignments) implements AssignmentResponse {}
        record Failure(String error) implements AssignmentResponse {}
}
