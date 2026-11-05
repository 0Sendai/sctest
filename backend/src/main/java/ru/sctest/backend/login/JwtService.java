package ru.sctest.backend.login;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import ru.sctest.backend.db.model.User;

@Service 
@Slf4j 
public class JwtService {
    private final static long EXPIRATION_DATE = 18000000;
    private final SecretKey key;

    public JwtService(@Value("${JWT_SECRET}") String jwtSecret) {
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("role", user.getRole());
        claims.put("id", user.getId());

        return Jwts.builder().subject(user.getName())
               .expiration(new Date(System.currentTimeMillis() + EXPIRATION_DATE))
               .claims(claims)
               .signWith(key)
               .compact();
               
    }

    public User extractUser(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(key)
            .build().parseSignedClaims(token).getPayload();
        } catch (Error e) {
            log.warn("Cannot extract user");
            return null;
        }
        String name = claims.get("name", String.class);
        String role = claims.get("role", String.class);
        Long id = claims.get("id", Long.class);

        return new User(id, name, role);
    }

}
