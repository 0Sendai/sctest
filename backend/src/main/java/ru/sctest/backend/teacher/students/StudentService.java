package ru.sctest.backend.teacher.students;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.model.UserRole;
import ru.sctest.backend.db.repo.UserRepository;

@Service
public class StudentService {

    @Autowired 
    private UserRepository userRepository;

    public List<StudentDto> getStudents() {
        List<User> students = userRepository.findAllByRole(UserRole.STUDENT);
        List<StudentDto> studentDtos = students.stream()
            .map(StudentDto::new).toList();
        return studentDtos;
    }
}
