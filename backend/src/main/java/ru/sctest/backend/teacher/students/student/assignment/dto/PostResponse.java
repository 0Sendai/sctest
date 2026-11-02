package ru.sctest.backend.teacher.students.student.assignment.dto;

import java.util.List;

public sealed interface PostResponse 
    permits PostResponse.Success, PostResponse.Fail
{
    record Success(Boolean success, List<UpdatedSlidesDto> updatedSlides) 
        implements PostResponse {}
    record Fail(String error) implements PostResponse {}
}
