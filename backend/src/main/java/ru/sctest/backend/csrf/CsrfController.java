package ru.sctest.backend.csrf;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class CsrfController {
    @GetMapping("/api/csrf")
    public ResponseEntity<Void> getCsrf() {
        return ResponseEntity.ok().build(); 
    }
}
