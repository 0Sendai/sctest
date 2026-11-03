package ru.sctest.backend.student.assignment.dto;

import java.util.List;
import java.util.Map;

public sealed interface GetResponse 
    permits GetResponse.Success, GetResponse.Fail
{
    record Success(
        AssignmentDto assignment,
        List<SlideDto> slides,
        AssignmentResultDto assignmentResult,
        Map<Long, Object> slideAnswers,
        Map<Long, String> slideComments
    ) implements GetResponse {}
    record Fail(String error) implements GetResponse {}
}
