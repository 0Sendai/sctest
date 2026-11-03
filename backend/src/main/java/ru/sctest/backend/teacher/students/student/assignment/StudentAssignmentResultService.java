package ru.sctest.backend.teacher.students.student.assignment;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import ru.sctest.backend.teacher.students.student.assignment.dto.SlideWithResultsDto;
import ru.sctest.backend.teacher.students.student.assignment.dto.StudentInfoResponse;
import ru.sctest.backend.teacher.students.student.assignment.dto.UpdatedSlidesDto;
import ru.sctest.backend.teacher.students.student.assignment.dto.StudentDto;
import ru.sctest.backend.teacher.students.student.assignment.dto.AssignmentDto;
import ru.sctest.backend.teacher.students.student.assignment.dto.CommentsDto;
import ru.sctest.backend.teacher.students.student.assignment.dto.CorrectnessDto;
import ru.sctest.backend.teacher.students.student.assignment.dto.PostRequestDto;
import ru.sctest.backend.teacher.students.student.assignment.dto.PostResponse;
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
        
        final List<SlideWithResultsDto> slideDtos = new ArrayList<>();
        final Map<Long, String> slideCommentsMap = new HashMap<>();

        for (int i = 0; i < slideResults.size(); i++) {
            Slide curSlide = slides.get(i);
            SlideResult curRes = slideResults.get(i);
            slideDtos.add(new SlideWithResultsDto(
                curSlide.getId(),
                curSlide.getType(),
                curSlide.getPosition(),
                curSlide.getContent(),
                curRes.getAnswer(),
                curRes.getIsCorrect(),
                curRes.getAnsweredAt()
            ));

            if (slideComments.size() > i)
                slideCommentsMap.put(
                    curSlide.getId(), 
                    slideComments.get(i).getComment()
                );
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

    @Transactional(rollbackFor = Exception.class)
    public PostResponse updateCorrectness(PostRequestDto requestBody) {
        Long studId = requestBody.studentId();
        if (studId == null) {
            return new PostResponse.Fail("student id is null");
        }
        Optional<User> studentOpt = userRepository.findById(requestBody.studentId());
        if (studentOpt.isEmpty()) {
            return new PostResponse.Fail("Can't find student");
        }
        final User student = studentOpt.get();

        UUID assignId = requestBody.assignmentId();
        if (assignId == null) {
            return new PostResponse.Fail("assignment id is null");
        }
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignId);
        if (assignmentOpt.isEmpty()) {
            return new PostResponse.Fail("Can't find assignment");
        }
        final Assignment assignment = assignmentOpt.get();

        Optional<AssignmentResult> assignResOpt = assignmentResultRepository
            .findByAssignmentAndStudent(assignment, student);
        if (assignResOpt.isEmpty()) {
            return new PostResponse.Fail("Can't find assignment result");
        }
        final AssignmentResult assignmentResult = assignResOpt.get();

        List<CommentsDto> comments = requestBody.comments();
        if (!comments.isEmpty()) {
            for (var comment: comments) {
                Slide slide = slideRepository.getReferenceById(comment.slideId());
                SlideComment slideComment = new SlideComment();
                slideComment.setAssignmentResult(assignmentResult);
                slideComment.setStudent(student);
                slideComment.setSlide(slide);
                slideComment.setComment(comment.comment());
                slideCommentRepository.upsertComment(slideComment);
            }
        }

        // TODO: revisit logic
        List<CorrectnessDto> correctnesses = requestBody.correctnesses();
        List<UpdatedSlidesDto> updatedSlides = new ArrayList<>();
        if (!correctnesses.isEmpty()) {
            for (var cor: correctnesses) {
                Optional<Slide> slideOpt = slideRepository.findById(cor.slideId());
                if (slideOpt.isEmpty()) {
                    return new PostResponse.Fail("Can't find slide");
                }
                Slide slide = slideOpt.get();
                SlideResult slideResult;
                Optional<SlideResult> slideResultOpt = slideResultRepository
                    .findByAssignmentResultAndSlide(assignmentResult, slide);
                if (slideResultOpt.isEmpty()) {
                    slideResult = new SlideResult();
                } else {
                    slideResult = slideResultOpt.get();
                }
                slideResult.setIsCorrect(cor.isCorrect());
                slideResultRepository.save(slideResult);
                updatedSlides.add(new UpdatedSlidesDto(slide.getId(), cor.isCorrect()));
            }
        }
        return new PostResponse.Success(
            true,
            updatedSlides
        );
    }
}
