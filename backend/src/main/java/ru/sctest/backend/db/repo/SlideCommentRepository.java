package ru.sctest.backend.db.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.SlideComment;

@Repository 
public interface SlideCommentRepository extends JpaRepository<SlideComment, Long> {
    public List<SlideComment> findByAssignmentResultOrderBySlide_Id(
        AssignmentResult assignmentResult
    );
}
