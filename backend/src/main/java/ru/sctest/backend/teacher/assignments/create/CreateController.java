package ru.sctest.backend.teacher.assignments.create;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/api/teacher/assignments/create")
public class CreateController {
    private final CreateService createService;

    public CreateController(CreateService createService) {
        this.createService = createService;
    }

    @GetMapping
    public List<StudentDto> getStudents() {
        return createService.getStudents();
    }

    //@PostMapping
    //public void create()
}
