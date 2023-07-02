package com.todo.apigateway.user;

import java.util.Collection;
import java.util.List;

import com.todo.apigateway.token.Token;
import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@Getter
@Setter
public class User implements UserDetails {

  @Column(name = "id", nullable = false)
  @Id
  private String id;
  @Column(name = "first_name", nullable = false)
  private String firstname;
  @Column(name = "last_name", nullable = false)
  private String lastname;
  @Column(name = "email", nullable = false, unique = true, length = 320)
  private String email;
  @Column(name = "password", nullable = false, length = 1000)
  private String password;

  @Column(name = "role")
  private Role role;
  @DBRef
  private List<Token> tokens;

  @Column(name = "lists")
  private Integer[] lists;

  // CHANGE ON FALSE
  @Column(name = "email_verified", nullable = false)
  private Boolean emailVerified = true;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
