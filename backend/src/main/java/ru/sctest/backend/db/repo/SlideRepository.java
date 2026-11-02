package ru.sctest.backend.db.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.sctest.backend.db.model.Assignment;
import ru.sctest.backend.db.model.Slide;

@Repository 
public interface SlideRepository extends JpaRepository<Slide, Long>{
    public List<Slide> findByAssignmentOrderById(Assignment assignment);
}
