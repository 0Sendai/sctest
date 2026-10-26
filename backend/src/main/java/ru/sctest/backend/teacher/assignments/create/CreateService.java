package ru.sctest.backend.teacher.assignments.create;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ru.sctest.backend.db.repo.UserRepository;
import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.model.UserRole;

@Service 
public class CreateService {
    private final UserRepository userRepository;

    public CreateService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<StudentDto> getStudents() {
        final List<User> students = userRepository.findAllByRole(UserRole.STUDENT);
        final List<StudentDto> studentDtos = students.stream()
        .map(StudentDto::new).collect(Collectors.toList());

        return studentDtos;
    }
}
