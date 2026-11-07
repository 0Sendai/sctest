package ru.sctest.backend.assignment.slideResult;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.sctest.backend.assignment.slideResult.dto.PostRequest;
import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.Slide;
import ru.sctest.backend.db.model.SlideResult;
import ru.sctest.backend.db.repo.AssignmentResultRepository;
import ru.sctest.backend.db.repo.SlideRepository;
import ru.sctest.backend.db.repo.SlideResultRepository;
import tools.jackson.databind.ObjectMapper;

@Service 
public class SlideResultUpdateService {

    @Autowired 
    private SlideRepository slideRepository;

    @Autowired 
    private AssignmentResultRepository assignmentResultRepository;

    @Autowired 
    private SlideResultRepository slideResultRepository;

    // TODO student
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSlideResult(PostRequest requestBody) throws Exception {
        Long slideId = requestBody.slideId();
        if (slideId == null) throw new Exception("slideId is null");
        Slide slide = slideRepository.findById(slideId)
            .orElseThrow(() -> new NoSuchElementException("Can't find slide"));
        
        Long assignResId = requestBody.resultId();
        if (assignResId == null) throw new Exception("resultId is null");
        AssignmentResult assignmentResult = assignmentResultRepository
            .findById(assignResId)
            .orElseThrow(() -> new NoSuchElementException("Can't find assignmentRes"));
        
        List<Slide> assignmentSlides = assignmentResult.getAssignment().getSlides();
        if (!assignmentSlides.stream().anyMatch(s -> s.getId() == slide.getId())) {
            throw new Exception("slide doesn't belong to the assignment");
        }

        Object answer = requestBody.answer();
        Boolean isCorrect = null;
        if (slide.getType().equals("quiz")) {
            final var content = slide.getContent();
            int correctIdx = (int)content.get("correctVariantIndex");
            List<?> variantsRaw = (List<?>)content.get("variants");
            List<String> variants = variantsRaw.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList();
            String answerStr = (String)answer;
            isCorrect = variants.indexOf(answerStr) == correctIdx ? true : false;
            // adding quotes
        }
        if (slide.getType().equals("quiz") || slide.getType().equals("text")) {
            ObjectMapper mapper = new ObjectMapper();
                answer = mapper.writeValueAsString(answer);
        }

        SlideResult slideRes = new SlideResult();
        slideRes.setAnswer(answer);
        slideRes.setAssignmentResult(assignmentResult);
        slideRes.setIsCorrect(isCorrect);
        slideRes.setSlide(slide);
        slideResultRepository.upsertSlideResult(slideRes);

        return true;
    }
}
