package ru.sctest.backend.teacher.assignments;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.model.Assignment;
import ru.sctest.backend.db.repo.UserRepository;

@RestController 
public class AssignmentController {
    private final UserRepository userRepository;

    public AssignmentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/teacher/assignments")
    public ResponseEntity<AssignmentResponse> getAssignments(@RequestParam Long id) {
        final Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body(new AssignmentResponse.Failure("bad user id"));
        
        
        final User user = userOpt.get();
        final List<Assignment> assignments = user.getAssignments();
        final List<AssignmentDto> assignmentDtos = new ArrayList<>(assignments.size());
        for (var assignment: assignments) {
            assignmentDtos.add(new AssignmentDto(
                assignment.getId(), assignment.getTitle()));
        }
        return ResponseEntity.status(HttpStatus.OK)
               .body(new AssignmentResponse.Success(assignmentDtos));
    }
}
