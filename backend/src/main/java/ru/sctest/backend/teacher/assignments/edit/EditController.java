package ru.sctest.backend.teacher.assignments.edit;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.sctest.backend.teacher.assignments.edit.dto.EditResponse;
import ru.sctest.backend.teacher.assignments.edit.dto.ResponseDto;

@RestController 
@RequestMapping("/api/teacher/assignments/edit")
public class EditController {
    @Autowired 
    private EditService editService;

    @GetMapping
    public ResponseEntity<EditResponse> getAssignment(@RequestParam UUID id) {
        ResponseDto response;
        try {
            response = editService.getAssignment(id);
        } catch (EditException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new EditResponse.Fail(e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.OK)
            .body(new EditResponse.Success(response));
    }

}
