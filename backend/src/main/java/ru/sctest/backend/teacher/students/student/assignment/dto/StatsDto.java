package ru.sctest.backend.teacher.students.student.assignment.dto;

public record StatsDto(
    Integer total,
    Integer answered,
    Integer correct,
    Integer incorrect,
    Integer unanswered
) {}
