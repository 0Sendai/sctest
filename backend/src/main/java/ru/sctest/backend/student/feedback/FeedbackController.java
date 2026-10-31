package ru.sctest.backend.student.feedback;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.sctest.backend.student.feedback.dto.FeedbackDto;

@RestController 
public class FeedbackController {

    @Autowired 
    private FeedbackService feedbackService;

    @GetMapping("/api/student/feedback")
    public ResponseEntity<FeedbackResponse> getFeedback(
        @RequestParam Long studId,
        @RequestParam UUID assignmentId
    )
    {
        FeedbackDto feedback;
        try {
            feedback = feedbackService.getFeedback(studId, assignmentId);
        } catch (FeedbackException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new FeedbackResponse.Fail(e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK)
            .body(new FeedbackResponse.Success(feedback));
    }
}
