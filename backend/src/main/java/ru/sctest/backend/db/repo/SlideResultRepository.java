package ru.sctest.backend.db.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.SlideResult;

@Repository 
public interface SlideResultRepository extends JpaRepository<SlideResult, Long> {
    public List<SlideResult> findByAssignmentResult(AssignmentResult assignmentResult);
}
