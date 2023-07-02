package com.todo.apigateway.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class CustomUserDetails implements UserDetails {

     String jwt;
    @Autowired
    private UserDetailsService userDetailsService;
    public String id;
    public String username;
    public String password;

     @Override
     public Collection<? extends GrantedAuthority> getAuthorities() {
          return null;
     }

     @Override
     public boolean isAccountNonExpired() {
          return false;
     }

     @Override
     public boolean isAccountNonLocked() {
          return false;
     }

     @Override
     public boolean isCredentialsNonExpired() {
          return false;
     }

     @Override
     public boolean isEnabled() {
          return false;
     }

//     public getUser() {
//
//     }
}