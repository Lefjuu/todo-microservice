package com.todo.apigateway.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Integer id;
    private Integer listId;
    private String userId;
    private String name;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
}