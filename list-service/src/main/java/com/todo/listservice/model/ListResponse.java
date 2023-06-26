package com.todo.listservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListResponse {
    private Integer id;
    private String userId;
    private Integer[] task;
}
