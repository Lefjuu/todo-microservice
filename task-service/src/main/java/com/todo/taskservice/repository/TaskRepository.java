package com.todo.taskservice.repository;

import com.todo.taskservice.model.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByListId(Integer listId);

    void deleteAllByIdIn(List<Integer> listIds);

    @Query("SELECT t FROM Task t WHERE t.id IN :taskIds")
    List<Task> findAllByTaskIds(List<Integer> taskIds);

    void deleteAllByListId(Integer listId);
}