package ru.sctest.backend.teacher.assignments.edit;

import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.sctest.backend.db.repo.AssignmentRepository;
import ru.sctest.backend.db.repo.AssignmentResultRepository;
import ru.sctest.backend.db.repo.SlideRepository;
import ru.sctest.backend.db.repo.UserRepository;
import ru.sctest.backend.db.model.Assignment;
import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.AssignmentResultStatus;
import ru.sctest.backend.db.model.Slide;
import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.model.UserRole;
import ru.sctest.backend.teacher.assignments.edit.dto.PostRequest;
import ru.sctest.backend.teacher.assignments.edit.dto.PostRequestSlideDto;
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

    @Autowired 
    private SlideRepository slideRepository;

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

    @Transactional(rollbackFor = Exception.class) 
    public Boolean editAssignment(PostRequest requestBody) throws Exception {
        UUID assignmentId = requestBody.assignmentId();
        if (assignmentId == null)
            throw new Exception("assignmentId is null");
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new NoSuchElementException("Can't find assignment"));

        final String title = requestBody.title();
        if (title != null && !assignment.getTitle().equals(title)) {
            assignment.setTitle(title);
        }
        final List<PostRequestSlideDto> slideDtos = requestBody.slides();
        final List<Slide> slides = new ArrayList<>();
        for (int i = 0; i < slideDtos.size(); i++) {
            final var curSlide = slideDtos.get(i);
            final String type = curSlide.type();
            final String question = curSlide.question();
            if (type     == null || type.isBlank() ||
                question == null || question.isBlank()) {
                    throw new Exception("Bad slide dto");
            }
            var slide = new Slide();
            Map<String, Object> content = new HashMap<>();
            content.put("question", question);
            if (type.equals("quiz")) {
                final List<String> variants = curSlide.variants();
                if (variants.isEmpty() || 
                    variants.stream().anyMatch((String str) -> str.isBlank())) {
                        throw new Exception("empty variant");
                }
                content.put("variants", variants);
                final var correctIdx = curSlide.correctVariantIndex();
                if (correctIdx < 0) {
                    throw new Exception("bad correct variant index");
                }
                content.put("correctVariantIndex", correctIdx);
            }
            slide.setPosition(i);
            slide.setContent(content);
            slide.setType(type);
            slide.setAssignment(assignment);
            slides.add(slide);
        }

        List<AssignmentResult> assignResults = 
            assignmentResultRepository.findAllByAssignment(assignment);
        assignResults.forEach(ar -> ar.setCurrentSlide(null));
        assignmentResultRepository.saveAllAndFlush(assignResults);
        assignment.getSlides().clear();
        slideRepository.flush();
        assignment.getSlides().addAll(slides);
        slideRepository.flush();

        assignment.getAssignmentResults().clear();
        assignmentResultRepository.flush();
        final List<Long> students = requestBody.studentIdsJson();
        if (!students.isEmpty()) { 
            List<AssignmentResult> assignmentResults = new ArrayList<>();
            for (var studentId: students) {
                final User student = userRepository.getReferenceById(studentId);
                var assignmentResult = new AssignmentResult();
                assignmentResult.setStatus(AssignmentResultStatus.NOT_STARTED);
                assignmentResult.setAssignment(assignment);
                assignmentResult.setStudent(student);
                assignmentResult.setIsGraded(false);
                assignmentResult.setCurrentSlide(slides.get(0));
                assignmentResults.add(assignmentResult);
            }
            assignment.getAssignmentResults().addAll(assignmentResults);
        }

        return true;
    }
}
