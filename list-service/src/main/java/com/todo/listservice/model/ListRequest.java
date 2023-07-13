package com.todo.listservice.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ListRequest {
    private String userId;
    private String name;
    private List<Integer> tasksIds;
}
