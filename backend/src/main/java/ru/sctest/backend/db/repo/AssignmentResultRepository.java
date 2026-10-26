package ru.sctest.backend.db.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.sctest.backend.db.model.AssignmentResult;

@Repository 
public interface AssignmentResultRepository 
    extends JpaRepository<AssignmentResult, Long>{
        
}
