package ru.sctest.backend.teacher.students.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class CreateStudentController {
    @Autowired 
    private CreateStudentService createService;

    @PostMapping("/api/teacher/students/create")
    public ResponseEntity<String> createUser(@RequestBody UserDto user) {
        Boolean response;
        try {
            response = createService.createStudent(
                user.name(), user.password()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
        if (!response) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unknown error");
        }
        return ResponseEntity.status(HttpStatus.OK).body("User was added");
    }
}
