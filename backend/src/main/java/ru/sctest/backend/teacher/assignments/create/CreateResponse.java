package ru.sctest.backend.teacher.assignments.create;

public sealed interface CreateResponse 
    permits CreateResponse.Success, CreateResponse.Failed {
        record Success(String status) implements CreateResponse {}
        record Failed(String error)   implements CreateResponse {}
}
