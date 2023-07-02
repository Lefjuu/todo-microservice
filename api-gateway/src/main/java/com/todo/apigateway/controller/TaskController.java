package com.todo.apigateway.controller;

import com.todo.apigateway.model.TaskResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
@PreAuthorize("hasRole('EMPLOYEE')")
@Slf4j
public class TaskController {

//    private final ObservationRegistry observationRegistry;

    private final WebClient.Builder webClientBuilder;

    private UserDetails userDetails;

//    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
    @GetMapping("/list/{listId}")
    @PreAuthorize("hasAuthority('employee:read')")
    public TaskResponse[] getTasksByListId(@PathVariable Integer listId) {
//        Observation taskServiceObservation = Observation.createNotStarted("task-service-lookup",
//                this.observationRegistry);
//        taskServiceObservation.lowCardinalityKeyValue("call", "task-service");
//        return taskServiceObservation.observe(() -> {
            TaskResponse[] taskResponseArray = webClientBuilder.build().get()
                    .uri("http://task-service/api/v1/task/list/",
                            uriBuilder -> uriBuilder.path("/" + listId).build())
                    .retrieve()
                    .bodyToMono(TaskResponse[].class)
                    .block();

            if (taskResponseArray != null) {
                return taskResponseArray;
            } else {
                throw new IllegalArgumentException("idk, idc");
            }
//        });
    }

    @PostMapping
    @PreAuthorize("hasAuthority('employee:create')")
    public String post() {
        return "POST:: employee controller";
    }
    @PutMapping
    @PreAuthorize("hasAuthority('employee:update')")
    public String put() {
        return "PUT:: employee controller";
    }
    @DeleteMapping
    @PreAuthorize("hasAuthority('employee:delete')")
    public String delete() {
        return "DELETE:: employee controller";
    }
}
