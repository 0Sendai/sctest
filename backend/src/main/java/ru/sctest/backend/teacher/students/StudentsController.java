package ru.sctest.backend.teacher.students;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class StudentsController {

    @Autowired 
    private StudentService studentService;

    @GetMapping("/api/teacher/students")
    public List<StudentDto> getStudents() {
        return studentService.getStudents();
    }
}
