package com.todo.taskservice.controller;

import com.todo.taskservice.exception.ListNotFoundException;
import com.todo.taskservice.model.TaskRequest;
import com.todo.taskservice.model.TaskResponse;
import com.todo.taskservice.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/list/{listId}")
    public TaskResponse createTask(@RequestBody TaskRequest taskRequest, @PathVariable Integer listId) throws ListNotFoundException {
        return taskService.createTask(taskRequest, listId);
    }

    @GetMapping("/list/{listId}")
    public List<TaskResponse> getAllTasksByListId(@PathVariable Integer listId) {
        return taskService.getAllTaskByListId(listId);
    }

    @DeleteMapping("/list/{listId}")
    public ResponseEntity deleteAllTasksByListId(@PathVariable Integer listId) {
        return taskService.deleteAllTaskByListId(listId);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity deleteTaskById(@PathVariable Integer taskId, @RequestHeader String userId) {
        return taskService.deleteTaskById(taskId, userId);
    }
}
