package com.todo.listservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "list")
public class ListLocal {

    @Id
    @SequenceGenerator(
            name="list_id_sequence",
            sequenceName = "list_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "list_id_sequence"
    )
    private Integer id;
    private String name;
    private String userId;
    private Integer[] task;
}
