package com.todo.apigateway.controller;

import com.todo.apigateway.model.*;
import com.todo.apigateway.service.ManagementService;
import com.todo.apigateway.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/management")
@PreAuthorize("hasRole('MANAGER')")
@Slf4j
public class ManagementController {

    @Autowired
    private final WebClient.Builder webClientBuilder;
    @Autowired
    private ManagementService managementService;

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('management:read')")
    public List<User> getAllEmployees() {
        return managementService.getAllEmployees();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getEmployeeList(@PathVariable String userId) {
        try {
            List<ManagementListResponse> listResponses = webClientBuilder.build()
                    .get()
                    .uri("http://list-service/api/v1/management/list/user/" + userId)
                    .retrieve()
                    .bodyToFlux(ManagementListResponse.class)
                    .collectList()
                    .block();

            if (listResponses == null || listResponses.isEmpty()) {
                return ResponseEntity.notFound()
                        .build();
            }

            List<Integer> allTaskIds = new ArrayList<>();
            for (ManagementListResponse listResponse : listResponses) {
                allTaskIds.addAll(listResponse.getTaskIds());
            }

            String taskIdsParam = String.join(",", allTaskIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList()));

            List<TaskResponse> tasks = webClientBuilder.build()
                    .get()
                    .uri("http://task-service/api/v1/management/task/array?array=" + taskIdsParam)
                    .retrieve()
                    .bodyToFlux(TaskResponse.class)
                    .collectList()
                    .block();

            if (tasks != null) {
                for (ManagementListResponse listResponse : listResponses) {
                    List<TaskResponse> matchingTasks = new ArrayList<>();
                    for (TaskResponse task : tasks) {
                        if (listResponse.getTaskIds()
                                .contains(task.getId())) {
                            matchingTasks.add(task);
                        }
                    }
                    listResponse.setTasks(matchingTasks);
                }
            }

            return ResponseEntity.ok(listResponses);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
    }

    @PostMapping("/user/{userId}/list")
    public ListResponse createListForUser(@PathVariable String userId, @RequestBody ListRequest listRequest) {
        ListResponse listResponse = webClientBuilder.build()
                .post()
                .uri("http://list-service/api/v1/management/list/user/" + userId)
                .body(BodyInserters.fromValue(listRequest))
                .retrieve()
                .bodyToMono(ListResponse.class)
                .block();

        return listResponse;
    }

    @PostMapping("/user/{userId}/list/{listId}/task")
    public TaskResponse createTaskForUser(@PathVariable Integer listId, @PathVariable String userId, @RequestBody ListRequest listRequest) {
        TaskResponse taskResponse = webClientBuilder.build()
                .post()
                .uri("http://task-service/api/v1/management/task/user/" + userId + "/list/" + listId)
                .body(BodyInserters.fromValue(listRequest))
                .retrieve()
                .bodyToMono(TaskResponse.class)
                .block();

        return taskResponse;
    }

    @GetMapping("/stats")
    public StatsResponse getStats() {
        return managementService.getStats();
    }


    @PutMapping
    public String put() {
        return "PUT:: management controller";
    }

    @DeleteMapping
    public String delete() {
        return "DELETE:: management controller";
    }
}
