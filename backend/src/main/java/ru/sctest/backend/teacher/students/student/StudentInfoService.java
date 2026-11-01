package ru.sctest.backend.teacher.students.student;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.model.Assignment;
import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.SlideResult;
import ru.sctest.backend.db.repo.AssignmentResultRepository;
import ru.sctest.backend.db.repo.SlideResultRepository;
import ru.sctest.backend.db.repo.UserRepository;
import ru.sctest.backend.teacher.students.student.dto.StudentDto;
import ru.sctest.backend.teacher.students.student.dto.AssignmentDto;
import ru.sctest.backend.teacher.students.student.dto.StudentInfoResponse;

@Service 
public class StudentInfoService {

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private AssignmentResultRepository assignmentResultRepository;

    @Autowired 
    private SlideResultRepository slideResultRepository;

    public StudentInfoResponse getStudentInfo(Long id) {
        Optional<User> studentOpt = userRepository.findById(id);
        if (studentOpt.isEmpty()) {
            return new StudentInfoResponse.Fail("Can't find student");
        }
        User student = studentOpt.get();
        List<AssignmentResult> assignmentResults = assignmentResultRepository
            .findAll();
        List<AssignmentDto> assignmentDtos = assignmentResults.stream().map(
            (AssignmentResult r) -> {
                List<SlideResult> slideResults = slideResultRepository
                    .findByAssignmentResult(r);
                Assignment assignment = r.getAssignment();
                return new AssignmentDto(
                    r.getId(),
                    assignment.getId(),
                    r.getStatus(),
                    r.getStartedAt(),
                    r.getCompletedAt(),
                    assignment.getTitle(),
                    slideResults.size(),
                    slideResults.stream().filter(sr -> sr.getIsCorrect() == true).count(),
                    slideResults.stream().filter(sr -> sr.getIsCorrect() == false).count()
                );
            }
        ).toList();
        return new StudentInfoResponse.Success(
            new StudentDto(student),
            assignmentDtos
        );
    }
}
