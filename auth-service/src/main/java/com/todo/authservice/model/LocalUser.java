package com.todo.authservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LocalUser {

    @Column(name = "id", nullable = false)
    @Id
    private String id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", nullable = false, length = 1000)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "role")
    private String role = "EMPLOYEE";

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "lists")
    private Integer[] lists;


    // CHANGE ON FALSE
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = true;
}
