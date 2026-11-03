package ru.sctest.backend.db.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.SlideComment;

@Repository 
public interface SlideCommentRepository extends JpaRepository<SlideComment, Long> {
    public List<SlideComment> findByAssignmentResultOrderBySlide_Id(
        AssignmentResult assignmentResult
    );

    @Modifying 
    @Transactional 
    @Query(value = """
        INSERT INTO slide_comments 
        (assignment_id, assignment_result_id, slide_id, student_id, comment)
        VALUES (
            :#{#comment.assignmentResult.assignment.id},
            :#{#comment.assignmentResult.id},
            :#{#comment.slide.id},
            :#{#comment.student.id},
            :#{#comment.comment}
        )
        ON CONFLICT (assignment_id, student_id, slide_id)
        DO UPDATE SET comment = EXCLUDED.comment
        """, nativeQuery = true)
    public void upsertComment(@Param("comment") SlideComment slideComment);
}
