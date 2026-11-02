package ru.sctest.backend.teacher.students.student.assignment.dto;

import java.util.List;
import java.util.Map;

public sealed interface StudentInfoResponse 
    permits StudentInfoResponse.Success, StudentInfoResponse.Fail {
        record Success(
            StudentDto student,
            AssignmentDto assignment,
            ResultDto result,
            List<SlideWithResultsDto> slides,
            Map<Long, String> slideComments,
            StatsDto stats
        )
            implements StudentInfoResponse {}
        record Fail(String error) implements StudentInfoResponse {}
}
