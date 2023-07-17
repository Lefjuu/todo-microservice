package com.todo.apigateway.controller;

import com.todo.apigateway.config.JwtAuthenticationFilter;
import com.todo.apigateway.model.TaskRequest;
import com.todo.apigateway.model.TaskResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
@PreAuthorize("hasRole('EMPLOYEE')")
@Slf4j
public class TaskController {

//    private final ObservationRegistry observationRegistry;

    private final WebClient.Builder webClientBuilder;

    @Autowired
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;

    @GetMapping("/{taskId}")
    @PreAuthorize("hasAuthority('employee:read')")
    public TaskResponse getTaskById(@PathVariable Integer taskId) {
        TaskResponse taskResponse = webClientBuilder.build()
                .get()
                .uri("http://task-service/api/v1/task/",
                        uriBuilder -> uriBuilder.path("/" + taskId)
                                .build())
                .retrieve()
                .bodyToMono(TaskResponse.class)
                .block();

        if (taskResponse != null) {
            return taskResponse;
        } else {
            throw new IllegalArgumentException("idk, idc");
        }
    }

    @GetMapping("/list/{listId}")
    @PreAuthorize("hasAuthority('employee:read')")
    public TaskResponse[] getTasksByListId(@PathVariable Integer listId) {
        TaskResponse[] taskResponseArray = webClientBuilder.build()
                .get()
                .uri("http://task-service/api/v1/task/list/",
                        uriBuilder -> uriBuilder.path("/" + listId)
                                .build())
                .retrieve()
                .bodyToMono(TaskResponse[].class)
                .block();

        if (taskResponseArray != null) {
            return taskResponseArray;
        } else {
            throw new IllegalArgumentException("idk, idc");
        }
    }

    @PostMapping("/list/{listId}")
    @PreAuthorize("hasAuthority('employee:create')")
    public Object createTask(@RequestBody TaskRequest taskRequest, @PathVariable Integer listId) {
        log.info("checkpoint 1");
        taskRequest.setUserId(jwtAuthenticationFilter.getUser()
                .getId());
        log.info("checkpoint 2");
        TaskResponse taskResponse = new TaskResponse();
        log.info(String.valueOf(taskRequest));
        log.info("checkpoint 3");
        try {
            taskResponse = webClientBuilder.build()
                    .post()
                    .uri("http://task-service/api/v1/task",
                            uriBuilder -> uriBuilder.path("/list/" + listId)
                                    .build())
                    .body(BodyInserters.fromValue(taskRequest))
                    .retrieve()
                    .bodyToMono(TaskResponse.class)
                    .block();
            log.info("checkpoint 4 ");
        } catch (WebClientResponseException ex) {
            log.error("Error during task creation: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body("List Not Found");
        } catch (Exception e) {
            log.error("Error during task creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error");
        }
        return taskResponse;
    }

    @PutMapping
    @PreAuthorize("hasAuthority('employee:update')")
    public String put() {
        return "PUT:: employee controller";
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAuthority('employee:delete')")
    public ResponseEntity deleteTaskById(@PathVariable Integer taskId) {
        try {
            ResponseEntity response = webClientBuilder.build()
                    .delete()
                    .uri("http://task-service/api/v1/task/{taskId}", taskId)
                    .header("userId", jwtAuthenticationFilter.getUser()
                            .getId())
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            log.info(String.valueOf(response.getStatusCode()));
            log.info(String.valueOf(response.getBody()));
            log.info(String.valueOf(response.getHeaders()));
            return response;

        } catch (WebClientResponseException ex) {
            String errorMessage = "Task With id: " + taskId + " not found.";
            log.info(errorMessage);
//            throw ex;
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(errorMessage);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Something went wrong. Please try again later.");
        }
    }
}
