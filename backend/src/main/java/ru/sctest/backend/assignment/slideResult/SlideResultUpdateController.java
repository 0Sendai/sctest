package ru.sctest.backend.assignment.slideResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.sctest.backend.assignment.slideResult.dto.PostRequest;
import ru.sctest.backend.assignment.slideResult.dto.PostResponse;

@Slf4j 
@RestController 
public class SlideResultUpdateController {

    @Autowired 
    private SlideResultUpdateService updateService;

    @PostMapping("/api/assignments/slideResult")
    public ResponseEntity<PostResponse> updateSlideResult(
        @RequestBody PostRequest requestBody
    ) 
    {
        Boolean result;
        try {
            result = updateService.updateSlideResult(requestBody);
        } catch (Exception e) {
            String msg = e.getMessage();
            log.warn(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new PostResponse.Fail(msg));
        }
        if (!result) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new PostResponse.Fail("Bad server response"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new PostResponse.Success("success"));
    }
}
