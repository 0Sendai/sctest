package ru.sctest.backend.teacher.assignments.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ru.sctest.backend.db.repo.AssignmentRepository;
import ru.sctest.backend.db.repo.AssignmentResultRepository;
import ru.sctest.backend.db.repo.UserRepository;
import ru.sctest.backend.db.model.Assignment;
import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.AssignmentResultStatus;
import ru.sctest.backend.db.model.Slide;
import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.model.UserRole;

@Service 
@Slf4j 
public class CreateAssignmentService {

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private AssignmentRepository assignmentRepository;

    @Autowired 
    private AssignmentResultRepository assignmentResultRepository;

    public List<StudentDto> getStudents() {
        final List<User> students = userRepository.findAllByRole(UserRole.STUDENT);
        final List<StudentDto> studentDtos = students.stream()
        .map(StudentDto::new).collect(Collectors.toList());

        return studentDtos;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean createAssignment(AssignmentDto assignmentDto, Long id) 
        throws AssignmentCreateException {
        String title = assignmentDto.title();
        if (title == null || title.isBlank()) {
            throw new AssignmentCreateException("bad title");
        }
        title = title.strip();

        List<Map<String,Object>> slides = assignmentDto.slides();
        if (slides == null || slides.isEmpty())
            throw new AssignmentCreateException("empty slides");

        List<Slide> resultSlides = new ArrayList<>(slides.size());
        for (int i = 0; i < slides.size(); i++) {
            final String question = (String)slides.get(i).get("question");
            if (question == null || question.isBlank())
                throw new AssignmentCreateException("question is blank");

            String type = (String)slides.get(i).get("type");
            if (type == null || type.isBlank())
                throw new AssignmentCreateException("type is blank");
            type = type.strip();
            Slide slide = new Slide();
            slide.setType(type);
            slide.setPosition(i);
            if (type.equals("text")) {
                Map<String, Object> content = Map.of(
                    "question", question
                );
                slide.setContent(content);

            } else if (type.equals("quiz")) {
                List<?> rawVariants = (List<?>)slides.get(i).get("variants");
                List<String> variants = new ArrayList<>(rawVariants.size());
                for (Object obj: rawVariants) {
                    if (obj instanceof String) {
                        String str = (String)obj;
                        str = str.strip();
                        if (str.isBlank())
                            throw new AssignmentCreateException("Empty string in variants");
                        variants.add(str);
                    }
                    else 
                        throw new AssignmentCreateException("Not a string in a variants");
                }
                Integer correctVariantIndex = (Integer)slides.get(i).get("correctVariantIndex");
                if (correctVariantIndex == null || correctVariantIndex < 0)
                    throw new AssignmentCreateException("Bad correctVariantIndex");
                Map<String, Object> content = Map.of(
                    "question", question,
                    "variants", variants,
                    "correctVariantIndex", correctVariantIndex
                );
                slide.setContent(content);

            }
            resultSlides.add(slide);
        }
        User teacher = userRepository.getReferenceById(id);
        Assignment assignment = new Assignment();
        assignment.setAuthor(teacher);
        assignment.setTitle(title);
        assignment.setSlides(resultSlides);
        assignmentRepository.save(assignment);
        log.info("assignment was added");

        List<Long> studentIds = assignmentDto.studentIds();
        if (studentIds == null || studentIds.isEmpty())
            return true;
        for (var studentId: studentIds) {
            User student = userRepository.getReferenceById(studentId);
            AssignmentResult ar = new AssignmentResult();
            ar.setStatus(AssignmentResultStatus.NOT_STARTED);
            ar.setStudent(student);
            ar.setAssignment(assignment);
            assignmentResultRepository.save(ar);
        }
        return true;
    }
}
