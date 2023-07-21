package com.todo.apigateway.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class StatsResponse {
    private Integer employeeCount;
    private Integer ListCount;
    private Integer taskCount;
}
