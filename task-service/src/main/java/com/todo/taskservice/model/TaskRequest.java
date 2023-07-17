package com.todo.taskservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    private Integer listId;
    private String userId;
    private String name;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
}
