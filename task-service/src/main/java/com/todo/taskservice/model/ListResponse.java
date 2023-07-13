package com.todo.taskservice.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ListResponse {
    private Integer id;
    private String name;
    private String userId;
    private List<Integer> tasksIds;
}
