package ru.sctest.backend.db.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.Assignment;
import ru.sctest.backend.db.model.User;

@Repository 
public interface AssignmentResultRepository 
    extends JpaRepository<AssignmentResult, Long>
{
    public Optional<AssignmentResult> findByAssignmentAndStudent(
        Assignment assignment, User student
    );

    public List<AssignmentResult> findAllByAssignment(Assignment assignment);
    public void deleteAllByAssignment(Assignment assignment);
}
