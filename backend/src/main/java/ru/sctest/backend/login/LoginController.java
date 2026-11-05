package ru.sctest.backend.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RestController 
public class LoginController {

    @Autowired 
    private LoginService loginService;

    @PostMapping("/api/login")
    public ResponseEntity<PostResponse> doLogin(
        @RequestBody PostRequest requestBody,
        HttpServletRequest request
    ) {
        String token;
        try {
            token = loginService.doLogin(requestBody);
        } catch (Exception e) {
            String msg = e.getMessage();
            log.warn(msg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new PostResponse.Fail(msg));
        }
        ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", token)
            .httpOnly(true)
            .secure(false)
            .maxAge(60 * 60 * 8)
            //.sameSite("Strict")
            .build();

        
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new PostResponse.Success("login successfully"));
    }
}
