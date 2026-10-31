package ru.sctest.backend.teacher.students.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.model.UserRole;
import ru.sctest.backend.db.repo.UserRepository;

@Service
@Slf4j 
@Transactional(rollbackOn = Exception.class)
public class CreateStudentService {

    @Autowired 
    private PasswordEncoder passwordEncoder;

    @Autowired 
    private UserRepository userRepository;

    public Boolean createStudent(String name, String password) throws Exception{
        User findedUser = userRepository.findByName(name);
        if (findedUser != null) {
            throw new Exception("User already exists");
        }
        User student = new User();
        student.setName(name);
        student.setRole(UserRole.STUDENT);
        String encodedPassword = passwordEncoder.encode(password);
        student.setPassword(encodedPassword);
        userRepository.save(student);
        log.info("User was added");
        return true;
    }
}
