package com.todo.authservice.controller;

import com.todo.authservice.exception.EmailFailureException;
import com.todo.authservice.exception.UserAlreadyExistsException;
import com.todo.authservice.exception.UserNotVerifiedException;
import com.todo.authservice.model.LoginBody;
import com.todo.authservice.model.LoginResponse;
import com.todo.authservice.model.RegistrationBody;
import com.todo.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.persistence.*;
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody) throws UserAlreadyExistsException {
        try {
            authService.registerUser(registrationBody);
            return new ResponseEntity<>("Account Created, Please Log in", HttpStatus.OK);
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
//        catch (EmailFailureException ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = null;
        try {
            jwt = authService.loginUser(loginBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        catch (UserNotVerifiedException ex) {
//            //response and email send
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .build();
//
//        }
//        catch (EmailFailureException ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .build();
//        }
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build();
        } else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            return ResponseEntity.ok(response);
        }
    }
}
