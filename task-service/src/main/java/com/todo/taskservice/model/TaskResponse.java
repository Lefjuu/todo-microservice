package com.todo.taskservice.model;

import lombok.*;

import java.time.LocalDateTime;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TaskResponse {
    private Integer id;
    private String userId;
    private Integer listId;
    private String name;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

}
