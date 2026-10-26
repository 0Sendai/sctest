package ru.sctest.backend.db.repo;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.model.UserRole;

@Repository 
public interface UserRepository extends JpaRepository<User, Long>{
    User findByName(String name);
    List<User> findAllByRole(UserRole role);
}
