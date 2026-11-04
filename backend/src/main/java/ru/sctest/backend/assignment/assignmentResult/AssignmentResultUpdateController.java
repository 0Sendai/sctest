package ru.sctest.backend.assignment.assignmentResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ru.sctest.backend.assignment.assignmentResult.dto.PostRequest;
import ru.sctest.backend.assignment.assignmentResult.dto.PostResponse;

@RestController 
public class AssignmentResultUpdateController {

    @Autowired 
    private AssignmentResultUpdateService updateService;

    @PostMapping("/api/assignments/assignmentResult")
    public ResponseEntity<PostResponse>updateAssignmentResult(
        @RequestBody PostRequest requestBody
    ) 
    {
        Boolean result;
        try {
            result = updateService.updateAssignmentResult(requestBody);
        } catch (Exception e) {
            String msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new PostResponse.Fail(msg));
        }
        if (!result) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new PostResponse.Fail("bad server response"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new PostResponse.Success("success"));
    }
}
