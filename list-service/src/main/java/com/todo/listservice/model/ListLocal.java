package com.todo.listservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.scheduling.config.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "list")
public class ListLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_id_sequence")
    @SequenceGenerator(name = "list_id_sequence", sequenceName = "list_id_sequence")
    private Integer id;

    private String name;
    private String userId;

//    @ElementCollection
//    @CollectionTable(name = "list_task_ids", joinColumns = @JoinColumn(name = "list_id"))
//    @Column(name = "task_id")
    private List<Integer> taskIds;
    private LocalDateTime createdAt;
}
