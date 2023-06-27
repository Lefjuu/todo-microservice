package com.todo.authservice.service;

import com.todo.authservice.model.LocalUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;


    public String generateJWT(LocalUser user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (1000L * expiryInSeconds)))
                .signWith(SignatureAlgorithm.HS256,
                        algorithmKey)
                .compact();
    }
}
