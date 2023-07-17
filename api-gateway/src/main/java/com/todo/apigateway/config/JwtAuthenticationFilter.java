package com.todo.apigateway.config;

import com.todo.apigateway.token.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final UserDetailsService userDetailsService;
    @Autowired
    private final TokenRepository tokenRepository;
    public String userEmail;
    public String userId;
    public CustomUserDetails customUserDetails = new CustomUserDetails();

    public CustomUserDetails getUser() {
        return this.customUserDetails;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                userId = getUserIdFromUserDetails(userDetails);
                log.info(userId);

                var isTokenValid = tokenRepository.findByToken(jwt)
                        .map(t -> !t.isExpired() && !t.isRevoked())
                        .orElse(false);

                if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                } else {
                    // Token is expired or not valid
                    ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired or not valid.");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write(errorResponse.getBody());
                    return;
                }
            } catch (ExpiredJwtException e) {
                // Token is expired
                ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired.");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(errorResponse.getBody());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    public String getUserIdFromUserDetails(UserDetails userDetails) {
        BeanUtils.copyProperties(userDetails, customUserDetails);
            return customUserDetails.getId();
    }
}
