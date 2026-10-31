package ru.sctest.backend.student;

import java.util.List;


public sealed interface StudentResponse 
    permits StudentResponse.Success, StudentResponse.Fail
{
    record Success(
        List<AssignmentResultDto> results
    ) implements StudentResponse 
    {}

    record Fail(String message) implements StudentResponse {}
}

