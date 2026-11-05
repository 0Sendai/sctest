package ru.sctest.backend.config.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.sctest.backend.db.model.User;
import ru.sctest.backend.login.JwtService;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override 
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException
    {
        String jwt = null;
        if (request.getCookies() != null) {
            for (Cookie cookie: request.getCookies()) {
                if ("AUTH-TOKEN".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        if (jwt == null || SecurityContextHolder
            .getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
        }

        User user = jwtService.extractUser(jwt);
        if (user != null) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            System.out.println("\n\nauthority: " + user.getRole().toString());
            authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(
                    user.getName(),
                    null,
                    authorities
            );
            authToken.setDetails(new WebAuthenticationDetailsSource()
                .buildDetails(request));
           SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}
