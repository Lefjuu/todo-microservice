package com.todo.taskservice.repository;

import com.todo.taskservice.model.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByListId(Integer listId);

//    void deleteByListId(Integer listId);

    @Transactional
    void deleteAllByListId(Integer listId);
}