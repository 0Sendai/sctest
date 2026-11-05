package ru.sctest.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import ru.sctest.backend.config.filter.CsrfCookieFilter;
import ru.sctest.backend.config.filter.JwtFilter;
import ru.sctest.backend.login.JwtService;

@Configuration 
@EnableWebSecurity 
public class SecurityConfig {

    @Autowired 
    private JwtService jwtService;

    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository
            .withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler requestHandler = 
            new CsrfTokenRequestAttributeHandler();

        http.sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .csrf(csrf -> csrf
            .csrfTokenRepository(tokenRepository)
            .csrfTokenRequestHandler(requestHandler)
            .ignoringRequestMatchers("/api/login")
        )
        .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
        .addFilterBefore(new JwtFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/login/**", "/api/csrf").permitAll()
            .requestMatchers("/api/teacher/**").hasRole("TEACHER")
            .requestMatchers("/api/student/**").hasRole("STUDENT")
            .anyRequest().authenticated()
        );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
