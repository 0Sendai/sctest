package ru.sctest.backend.student.assignment;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.sctest.backend.student.assignment.dto.GetResponse;

@Slf4j
@RestController 
public class AssignmentSolveController {

    @Autowired 
    private AssignmentSolveService solveService;
    
    @GetMapping("/api/student/assignment")
    public ResponseEntity<GetResponse> getAssignment(
        @RequestParam UUID assignmentId,
        @RequestParam Long studentId
    ) 
{
        GetResponse response;
        try {
            response = solveService.getAssignment(assignmentId, studentId);
        } catch (Exception e) {
            String msg = e.getMessage();
            log.warn(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GetResponse.Fail(msg));
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
