package ru.sctest.backend.teacher.students.student.assignment;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.sctest.backend.db.model.Assignment;
import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.Slide;
import ru.sctest.backend.db.model.SlideComment;
import ru.sctest.backend.db.model.SlideResult;
import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.repo.AssignmentResultRepository;
import ru.sctest.backend.db.repo.AssignmentRepository;
import ru.sctest.backend.db.repo.SlideCommentRepository;
import ru.sctest.backend.db.repo.SlideRepository;
import ru.sctest.backend.db.repo.SlideResultRepository;
import ru.sctest.backend.db.repo.UserRepository;
import ru.sctest.backend.teacher.students.student.assignment.dto.SlideDto;
import ru.sctest.backend.teacher.students.student.assignment.dto.StudentInfoResponse;
import ru.sctest.backend.teacher.students.student.assignment.dto.StudentDto;
import ru.sctest.backend.teacher.students.student.assignment.dto.AssignmentDto;
import ru.sctest.backend.teacher.students.student.assignment.dto.ResultDto;
import ru.sctest.backend.teacher.students.student.assignment.dto.StatsDto;

@Service 
public class StudentAssignmentResultService {

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private AssignmentRepository assignmentRepository;

    @Autowired 
    private AssignmentResultRepository assignmentResultRepository;

    @Autowired 
    private SlideResultRepository slideResultRepository;

    @Autowired 
    private SlideRepository slideRepository;

    @Autowired 
    private SlideCommentRepository slideCommentRepository;

    public StudentInfoResponse getStudentResults(Long studentId, UUID assignmentId) {
        Optional<User> studentOpt = userRepository
            .findById(studentId);
        if (studentOpt.isEmpty()) {
            return new StudentInfoResponse.Fail("Can't find student");
        }
        final User student = studentOpt.get();

        Optional<Assignment> assignmentOpt = assignmentRepository
            .findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            return new StudentInfoResponse.Fail("Can't find assignment");
        }
        final Assignment assignment = assignmentOpt.get();

        Optional<AssignmentResult> assignmentResultOpt = assignmentResultRepository
            .findByAssignmentAndStudent(assignment, student);
        if (assignmentOpt.isEmpty()) {
            return new StudentInfoResponse.Fail("Can't find assignment");
        }
        final AssignmentResult assignmentResult = assignmentResultOpt.get();
        
        final List<Slide> slides = slideRepository
            .findByAssignmentOrderById(assignment);
        final List<SlideResult> slideResults = 
            slideResultRepository.findByAssignmentResultOrderBySlide_Id(assignmentResult);
        final List<SlideComment> slideComments =
            slideCommentRepository.findByAssignmentResultOrderBySlide_Id(assignmentResult);
        
        final List<SlideDto> slideDtos = new ArrayList<>();
        final Map<Long, String> slideCommentsMap = new HashMap<>();

        for (int i = 0; i < slides.size(); i++) {
            Slide curSlide = slides.get(i);
            SlideResult curRes = slideResults.get(i);
            slideDtos.add(new SlideDto(
                curSlide.getId(),
                curSlide.getType(),
                curSlide.getPosition(),
                curSlide.getContent(),
                curRes.getAnswer(),
                curRes.getIsCorrect(),
                curRes.getAnsweredAt()
            ));

            slideCommentsMap.put(curSlide.getId(), slideComments.get(i).getComment());
        }

        Integer answered = 0, correct = 0, incorrect = 0;
        final Integer total = slides.size();
        for (var slide: slideResults) {
            answered++;
            if (slide.getIsCorrect())
                correct++;
            else
                incorrect++;
        }
        final Integer unaswered = total - answered;
        
        return new StudentInfoResponse.Success(
            new StudentDto(student),
            new AssignmentDto(assignment),
            new ResultDto(assignmentResult),
            slideDtos,
            slideCommentsMap,
            new StatsDto(total, answered, correct, incorrect, unaswered)
        );
    }
}
