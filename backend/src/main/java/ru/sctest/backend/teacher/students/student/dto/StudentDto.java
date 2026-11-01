package ru.sctest.backend.teacher.students.student.dto;

import ru.sctest.backend.db.model.User;

public record StudentDto(Long id, String name) {
    public StudentDto(User user) {
        this(user.getId(), user.getName());
    }
}
