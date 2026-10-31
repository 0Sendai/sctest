package ru.sctest.backend.student;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.repo.UserRepository;

@RestController 
public class StudentController {

    @Autowired 
    private UserRepository userRepository;

    @GetMapping("/api/student")
    public ResponseEntity<StudentResponse> getAssignments(@RequestParam Long id) {
        Optional<User> studentOpt = userRepository.findById(id);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StudentResponse.Fail("User not found"));
        }
        User student = studentOpt.get();
        List<AssignmentResult> assignmentResults = student.getAssignmentResults();
        if (assignmentResults.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StudentResponse.Fail("Assignments not found"));
        }
        List<AssignmentResultDto> assignmentResultDtos = 
            assignmentResults.stream().map(AssignmentResultDto::new).toList();

        if (assignmentResultDtos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StudentResponse.Fail("Can't convert assignmentResults"));
        }
        return ResponseEntity.status(HttpStatus.OK)
            .body(new StudentResponse.Success(assignmentResultDtos));
    }
}
