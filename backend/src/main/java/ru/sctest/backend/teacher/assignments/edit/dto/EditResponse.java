package ru.sctest.backend.teacher.assignments.edit.dto;

public sealed interface EditResponse 
    permits EditResponse.Success, EditResponse.Fail 
{
    record Success(ResponseDto data) implements EditResponse {}
    record Fail(String error) implements EditResponse {}
}
