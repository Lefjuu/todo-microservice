package com.todo.apigateway.controller;

import com.todo.apigateway.auth.AuthenticationService;
import com.todo.apigateway.exception.InvalidCredentialsException;
import com.todo.apigateway.model.AuthenticationRequest;
import com.todo.apigateway.model.AuthenticationResponse;
import com.todo.apigateway.model.LoginRequest;
import com.todo.apigateway.model.RegisterRequest;
import com.todo.apigateway.exception.EmailNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        log.info("hi");
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            return ResponseEntity.ok(authenticationService.loginUser(loginRequest));
        } catch (EmailNotFoundException | InvalidCredentialsException ex) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, ServletException {
        authenticationService.refreshToken(request, response);
    }
}
