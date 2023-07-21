package com.todo.taskservice.controller;

import com.todo.taskservice.exception.ListNotFoundException;
import com.todo.taskservice.model.TaskRequest;
import com.todo.taskservice.model.TaskResponse;
import com.todo.taskservice.service.ManagementTaskService;
import com.todo.taskservice.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/management/task")
@Slf4j
public class ManagementTaskController {

    @Autowired
    private ManagementTaskService managementTaskService;

    @GetMapping("/array")
    public ResponseEntity<List<TaskResponse>> getAllTasksByListId(@RequestParam("array") List<Integer> taskIds) {
        try {
            log.info(taskIds.toString());
            List<TaskResponse> tasks = managementTaskService.getAllTaskByTaskIdsArray(taskIds);
            return ResponseEntity.ok(tasks);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/user/{userId}/list/{listId}")
    public TaskResponse createTask(@PathVariable String userId, @PathVariable Integer listId, @RequestBody TaskRequest taskRequest) throws ListNotFoundException {
        return managementTaskService.createTaskForUser(userId, listId, taskRequest);
    }

    @GetMapping("/task-count")
    public ResponseEntity<Map<String, Integer>> getListCount() {
        Integer listCount = managementTaskService.getTaskCount();
        Map<String, Integer> response = new HashMap<>();
        response.put("count", listCount);
        return ResponseEntity.ok(response);
    }
}
