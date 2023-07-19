package com.todo.taskservice.controller;

import com.todo.taskservice.exception.ListNotFoundException;
import com.todo.taskservice.exception.TaskNotFoundException;
import com.todo.taskservice.model.Task;
import com.todo.taskservice.model.TaskRequest;
import com.todo.taskservice.model.TaskResponse;
import com.todo.taskservice.service.TaskService;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/{taskId}")
    public Optional<Task> getTaskById(@PathVariable Integer taskId) {
        return taskService.getTaskById(taskId);
    }

    @PostMapping("/list/{listId}")
    public TaskResponse createTask(@RequestBody TaskRequest taskRequest, @PathVariable Integer listId, @RequestHeader String userId) throws ListNotFoundException {
        return taskService.createTask(taskRequest, listId, userId);
    }

    @GetMapping("/list/{listId}")
    public List<TaskResponse> getAllTasksByListId(@PathVariable Integer listId) {
        return taskService.getAllTaskByListId(listId);
    }

    @PatchMapping("/list/{listId}")
    @Transactional
    public ResponseEntity deleteAllTasksById(@RequestBody List<Integer> tasksArray) {
        return taskService.deleteAllTasksById(tasksArray);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity deleteTaskById(@PathVariable Integer taskId, @RequestHeader String userId) {
        return taskService.deleteTaskById(taskId, userId);
    }

    @PatchMapping("/{taskId}")
    @Transactional
    public TaskResponse updateTask(@PathVariable Integer taskId, @RequestBody TaskRequest requestBody, @RequestHeader String userId) throws TaskNotFoundException {
        return taskService.updateTask(taskId, requestBody, userId);
    }

    @PatchMapping("/{taskId}/reverse-complete")
    @Transactional
    public TaskResponse setTaskReverseComplete(@PathVariable Integer taskId, @RequestHeader String userId) throws TaskNotFoundException {
        return taskService.setTaskReverseComplete(taskId, userId);
    }
}
