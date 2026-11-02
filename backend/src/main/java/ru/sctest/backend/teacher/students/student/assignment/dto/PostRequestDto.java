package ru.sctest.backend.teacher.students.student.assignment.dto;

import java.util.List;
import java.util.UUID;

public record PostRequestDto(
    UUID assignmentId,
    Long studentId,
    List<CommentsDto> comments,
    List<CorrectnessDto> correctnesses
) {

}
