package ru.sctest.backend.teacher.students.student.dto;

import java.util.List;

public sealed interface StudentInfoResponse 
    permits StudentInfoResponse.Success, StudentInfoResponse.Fail {
        record Success(StudentDto student, List<AssignmentDto> assignments)
            implements StudentInfoResponse {}
        record Fail(String error) implements StudentInfoResponse {}
}
