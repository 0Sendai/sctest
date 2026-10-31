package ru.sctest.backend.teacher.assignments.edit;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.sctest.backend.db.repo.AssignmentRepository;
import ru.sctest.backend.db.repo.AssignmentResultRepository;
import ru.sctest.backend.db.repo.UserRepository;
import ru.sctest.backend.db.model.Assignment;
import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.Slide;
import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.model.UserRole;
import ru.sctest.backend.teacher.assignments.edit.dto.ResponseDto;
import ru.sctest.backend.teacher.assignments.edit.dto.SlideDto;
import ru.sctest.backend.teacher.assignments.edit.dto.StudentDto;

@Service 
public class EditService {
    @Autowired 
    private AssignmentRepository assignmentRepository;

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private AssignmentResultRepository assignmentResultRepository;

    public ResponseDto getAssignment(UUID id) throws EditException {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(id);
        if (assignmentOpt.isEmpty())
            throw new EditException("Can't find assignment by id");
        Assignment assignment = assignmentOpt.get();
        List<User> students = userRepository.findAllByRole(UserRole.STUDENT);
        List<Slide> slides = assignment.getSlides();
        List<StudentDto> studentDtos = students.stream().map(StudentDto::new)
            .toList();
        List<SlideDto> slideDtos = slides.stream().map(SlideDto::new).toList();
        List<AssignmentResult> assignmentResults = 
            assignmentResultRepository.findAllByAssignment(assignment);
        List<Long> assignedStudentIds = assignmentResults.stream()
            .map(ar -> ar.getStudent().getId()).toList();

        return new ResponseDto(
            assignment.getTitle(),
            slideDtos,
            studentDtos,
            assignedStudentIds
        );
    }
}
