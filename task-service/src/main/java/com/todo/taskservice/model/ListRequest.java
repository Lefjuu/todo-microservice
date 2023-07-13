package com.todo.taskservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListRequest {
    private String userId;
    private String name;
    private List<Integer> tasksIds;
}
