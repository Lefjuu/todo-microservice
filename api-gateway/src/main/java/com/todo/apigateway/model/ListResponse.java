package com.todo.apigateway.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ListResponse {
    private Integer id;
    private String name;
    private String userId;
    private List<Integer> taskIds;
    private LocalDateTime createdAt;
}
