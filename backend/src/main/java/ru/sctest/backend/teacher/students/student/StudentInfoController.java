package ru.sctest.backend.teacher.students.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.sctest.backend.teacher.students.student.dto.StudentInfoResponse;

@RestController 
public class StudentInfoController {
    @Autowired 
    private StudentInfoService studentInfoService;

    @GetMapping("/api/teacher/students/student")
    public ResponseEntity<StudentInfoResponse> getStudentInfo(
        @RequestParam Long id
    )
{
    StudentInfoResponse response = studentInfoService.getStudentInfo(id);
    if (response instanceof StudentInfoResponse.Fail) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
    return ResponseEntity.status(HttpStatus.OK).body(response);
}

}
