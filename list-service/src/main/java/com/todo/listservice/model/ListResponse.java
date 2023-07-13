package com.todo.listservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListResponse {
    private Integer id;
    private String name;
    private String userId;
    private List<Integer> taskIds;
}
