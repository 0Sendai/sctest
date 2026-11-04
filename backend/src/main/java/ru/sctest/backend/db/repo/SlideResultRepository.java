package ru.sctest.backend.db.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.Slide;
import ru.sctest.backend.db.model.SlideResult;

@Repository 
public interface SlideResultRepository extends JpaRepository<SlideResult, Long> {
    public List<SlideResult> findByAssignmentResult(AssignmentResult assignmentResult);
    public List<SlideResult> findByAssignmentResultOrderBySlide_Id(
        AssignmentResult assignmentResult
    );

    public Optional<SlideResult> findByAssignmentResultAndSlide(
        AssignmentResult assignmentResult,
        Slide slide
    );

    @Modifying 
    @Transactional 
    @Query(value = """
        INSERT INTO slide_results (result_id, slide_id, answer, is_correct)
        VALUES (
            :#{#slideRes.assignmentResult.id},
            :#{#slideRes.slide.id},
            CAST(:#{#slideRes.answer} AS jsonb),
            :#{#slideRes.isCorrect}
        )
        ON CONFLICT (result_id, slide_id)
        DO UPDATE SET answer = EXCLUDED.answer, is_correct = EXCLUDED.is_correct
        """, nativeQuery = true)
    public void upsertSlideResult(@Param("slideRes") SlideResult slideResult);
}
