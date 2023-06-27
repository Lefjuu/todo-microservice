package com.todo.taskservice.controller;

import com.todo.taskservice.model.TaskRequest;
import com.todo.taskservice.model.TaskResponse;
import com.todo.taskservice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping()
    public TaskResponse createTask(@RequestBody TaskRequest taskRequest) {
        return taskService.createTask(taskRequest);
    }

    @GetMapping("/list/{listId}")
    public List<TaskResponse> getAllTasksByListId(@PathVariable Integer listId) {
        return taskService.getAllTaskByListId(listId);
    }

    @DeleteMapping("/list/{listId}")
    public ResponseEntity deleteAllTasksByListId(@PathVariable Integer listId) {
        return taskService.deleteAllTaskByListId(listId);
    }
}
