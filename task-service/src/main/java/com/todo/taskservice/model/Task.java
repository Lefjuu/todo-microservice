package com.todo.taskservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer listId;
    private String userId;
    private String name;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
}