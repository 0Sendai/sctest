package ru.sctest.backend.teacher.assignments.edit.dto;

import java.util.List;


public record ResponseDto(
    String title,
    List<SlideDto> slides,
    List<StudentDto> students,
    List<Long> assignedStudentIds
) {

}


