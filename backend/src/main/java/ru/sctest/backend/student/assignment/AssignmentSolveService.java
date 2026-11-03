package ru.sctest.backend.student.assignment;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.AssignmentResultStatus;
import ru.sctest.backend.db.model.Assignment;
import ru.sctest.backend.db.model.Slide;
import ru.sctest.backend.db.model.SlideComment;
import ru.sctest.backend.db.model.SlideResult;
import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.repo.AssignmentRepository;
import ru.sctest.backend.db.repo.AssignmentResultRepository;
import ru.sctest.backend.db.repo.UserRepository;
import ru.sctest.backend.student.assignment.dto.AssignmentDto;
import ru.sctest.backend.student.assignment.dto.AssignmentResultDto;
import ru.sctest.backend.student.assignment.dto.GetResponse;
import ru.sctest.backend.student.assignment.dto.SlideDto;

@Service 
public class AssignmentSolveService {

    @Autowired 
    private AssignmentRepository assignmentRepository;

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private AssignmentResultRepository assignmentResultRepository;

    @Transactional(rollbackFor = Exception.class)
    public GetResponse getAssignment(UUID assignmentId, Long studentId) 
        throws Exception 
    {
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new NoSuchElementException("can't find assignment"));
        
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new NoSuchElementException("Can't find student"));

        List<Slide> slides = assignment.getSlides();
        if (slides.isEmpty())
            throw new NoSuchElementException("Slides is empty");
        List<SlideDto> cleanSlides = slides.stream().map(slide -> {
            Map<String, Object> slideContent = slide.getContent();
            Map<String, Object> content = new HashMap<>();
            content.put("question", slideContent.get("question"));
            if (slide.getType().equals("quiz")) 
                content.put("variants", slideContent.get("variants"));
            return new SlideDto(
                slide.getId(), 
                slide.getType(), 
                slide.getPosition(),
                content
            );
        }).toList();

        AssignmentResult assignmentResult = assignmentResultRepository
            .findByAssignmentAndStudent(assignment, student)
            .orElseThrow(() -> new NoSuchElementException("Can't find assignmentResult"));
        
        assignmentResult.setStatus(AssignmentResultStatus.IN_PROGRESS);
        assignmentResultRepository.save(assignmentResult);

        List<SlideResult> slideResults = assignmentResult.getSlideResults();
        Map<Long, Object> slideAnswers = new HashMap<>();
        slideResults
            .forEach(sr -> slideAnswers.put(sr.getSlide().getId(), sr.getAnswer()));
        
        List<SlideComment> slideComments = assignmentResult.getSlideComments();
        Map<Long, String> slideCommentsMap = new HashMap<>();
        slideComments
            .forEach(sc -> slideCommentsMap.put(sc.getSlide().getId(), sc.getComment()));
        
        return new GetResponse.Success(
            new AssignmentDto(assignment),
            cleanSlides,
            new AssignmentResultDto(assignmentResult),
            slideAnswers,
            slideCommentsMap
        );
    }
}
