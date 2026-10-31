package ru.sctest.backend.db.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.sctest.backend.db.model.Assignment;

@Repository 
public interface AssignmentRepository extends JpaRepository<Assignment, UUID>{

}
