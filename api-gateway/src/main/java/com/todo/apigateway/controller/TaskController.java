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
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
@PreAuthorize("hasRole('EMPLOYEE')")
@Slf4j
public class TaskController {

    private final WebClient.Builder webClientBuilder;
    @Autowired
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

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
        taskRequest.setUserId(jwtAuthenticationFilter.getUser()
                .getId());
        TaskResponse taskResponse = new TaskResponse();
        try {
            taskResponse = webClientBuilder.build()
                    .post()
                    .uri("http://task-service/api/v1/task",
                            uriBuilder -> uriBuilder.path("/list/" + listId)
                                    .build())
                    .header("userId", jwtAuthenticationFilter.getUser()
                            .getId())
                    .body(BodyInserters.fromValue(taskRequest))
                    .retrieve()
                    .bodyToMono(TaskResponse.class)
                    .block();
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

            return response;

        } catch (WebClientResponseException ex) {
            String errorMessage = "Task With id: " + taskId + " not found.";
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(errorMessage);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Something went wrong. Please try again later.");
        }
    }


    @PatchMapping("/{taskId}")
    @PreAuthorize("hasAuthority('employee:update')")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Integer taskId, @RequestBody TaskRequest requestBody) {
        try {
            ResponseEntity<TaskResponse> response = webClientBuilder.build()
                    .patch()
                    .uri("http://task-service/api/v1/task/{taskId}", taskId)
                    .header("userId", jwtAuthenticationFilter.getUser()
                            .getId())
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> {
                        String errorMessage = "Task with id: " + taskId + " not found.";
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage));
                    })
                    .toEntity(TaskResponse.class)
                    .block();

            log.info("Status Code: " + response.getStatusCodeValue());
            log.info("Response Body: " + response.getBody());
            log.info("Response Headers: " + response.getHeaders());
            return response;

        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        } catch (WebClientResponseException ex) {
            String errorMessage = "Error while processing the request.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PatchMapping("/{taskId}/reverse-complete")
    @PreAuthorize("hasAuthority('employee:update')")
    public ResponseEntity<TaskResponse> setTaskReverseComplete(@PathVariable Integer taskId) {
        try {
            ResponseEntity<TaskResponse> response = webClientBuilder.build()
                    .patch()
                    .uri("http://task-service/api/v1/task/{taskId}/reverse-complete", taskId)
                    .header("userId", jwtAuthenticationFilter.getUser()
                            .getId())
                    .retrieve()
                    .toEntity(TaskResponse.class)
                    .block();

            return response;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception ex) {
            log.info(String.valueOf(ex));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
