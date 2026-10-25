package ru.sctest.backend.db.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.sctest.backend.db.model.User;

@Repository 
public interface UserRepository extends JpaRepository<User, Long>{
    User findByName(String name);
}
