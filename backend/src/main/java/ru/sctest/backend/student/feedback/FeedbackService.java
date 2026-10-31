package ru.sctest.backend.student.feedback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.sctest.backend.db.repo.AssignmentRepository;
import ru.sctest.backend.db.repo.AssignmentResultRepository;
import ru.sctest.backend.db.repo.UserRepository;
import ru.sctest.backend.student.feedback.dto.FeedbackDto;
import ru.sctest.backend.db.model.Assignment;
import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.SlideComment;
import ru.sctest.backend.db.model.SlideResult;
import ru.sctest.backend.db.model.User;

import ru.sctest.backend.student.feedback.dto.ResultDto;
import ru.sctest.backend.student.feedback.dto.SlideDto;

@Service 
public class FeedbackService {

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private AssignmentRepository assignmentRepository;

    @Autowired 
    private AssignmentResultRepository assignmentResultRepository;

    public FeedbackDto getFeedback(Long studId, UUID assignmentId) 
        throws FeedbackException
    {
        Optional<User> studentOpt = userRepository.findById(studId);
        if (studentOpt.isEmpty()) {
            throw new FeedbackException("Can't find user by id");
        }
        User student = studentOpt.get();

        Optional<Assignment> assignmentOpt = assignmentRepository
            .findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            throw new FeedbackException("Can't find assignment by id");
        }
        Assignment assignment = assignmentOpt.get();

        Optional<AssignmentResult> assignmentResultOpt = assignmentResultRepository
            .findByAssignmentAndStudent(assignment, student);
        if (assignmentResultOpt.isEmpty()) {
            throw new FeedbackException("can't find assignmentResult");
        }
        AssignmentResult assignmentResult = assignmentResultOpt.get();

        List<SlideResult> slideResults = assignmentResult.getSlideResults();
        List<SlideComment> slideComments = assignmentResult.getSlideComments();

        ResultDto resultDto = new ResultDto(assignmentResult);
        List<SlideDto> slideDtos = assignment.getSlides().stream()
            .map(SlideDto::from).toList();
        if (slideDtos.contains(null))
            throw new FeedbackException("null in slides");

        Map<Long, Object> answers = new HashMap<>();
        for (var answer: slideResults) {
            answers.put(answer.getSlide().getId(), answer.getAnswer());
        }
        Map<Long, String> comments = new HashMap<>();
        for (var comment: slideComments) {
            comments.put(comment.getSlide().getId(), comment.getComment());
        }
        return new FeedbackDto(
            assignment.getTitle(),
            resultDto,
            slideDtos,
            answers,
            comments
        );
    }
}
