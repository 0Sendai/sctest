package ru.sctest.backend.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.sctest.backend.db.model.User;
import ru.sctest.backend.db.repo.UserRepository;

@Service 
public class LoginService {
    @Autowired 
    private JwtService jwtService;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    @Autowired 
    private UserRepository userRepository;

    public String doLogin(PostRequest requestBody) throws Exception {
        String name = requestBody.name();
        String password = requestBody.password();
        if (name == null || password == null || 
            name.isBlank() || password.isBlank()) {
                throw new Exception("empty user data");
        }
        User user = userRepository.findByName(name)
            .orElseThrow(() -> new Exception("no such user"));
    
        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtService.generateToken(user);
        }
        throw new Exception("Wrong password");
    }
}
