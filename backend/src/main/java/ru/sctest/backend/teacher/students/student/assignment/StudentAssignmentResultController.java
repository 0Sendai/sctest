package ru.sctest.backend.teacher.students.student.assignment;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.sctest.backend.teacher.students.student.assignment.dto.StudentInfoResponse;

@RestController 
@RequestMapping("/api/teacher/students/student/assignment")
public class StudentAssignmentResultController {

    @Autowired 
    private StudentAssignmentResultService studentAssignmentResultService;

    @GetMapping
    public ResponseEntity<StudentInfoResponse> getStudentResult(
        @RequestParam Long studentId,
        @RequestParam UUID assignmentId
    )
    {
        StudentInfoResponse response = studentAssignmentResultService
            .getStudentResults(studentId, assignmentId);

        HttpStatus status;
        if (response instanceof StudentInfoResponse.Fail) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            status = HttpStatus.OK;
        }
        
        return ResponseEntity.status(status).body(response);
    }
}
