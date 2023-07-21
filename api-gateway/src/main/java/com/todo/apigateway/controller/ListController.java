package com.todo.apigateway.controller;

import com.todo.apigateway.config.CustomUserDetails;
import com.todo.apigateway.config.JwtAuthenticationFilter;
import com.todo.apigateway.model.ListRequest;
import com.todo.apigateway.model.ListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/list")
@PreAuthorize("hasRole('EMPLOYEE')")
@Slf4j
public class ListController {

    @Autowired
    private final WebClient.Builder webClientBuilder;
    @Autowired
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @GetMapping()
    @PreAuthorize("hasAuthority('employee:create')")
    public ListResponse[] getUserLists() {
        ListResponse[] listResponseArray = webClientBuilder.build()
                .get()
                .uri("http://list-service/api/v1/list/user",
                        uriBuilder -> uriBuilder.path("/" + jwtAuthenticationFilter.getUser()
                                        .getId())
                                .build())
                .header("userId", jwtAuthenticationFilter.getUser()
                        .getId())
                .retrieve()
                .bodyToMono(ListResponse[].class)
                .block();

        if (listResponseArray != null) {
            return listResponseArray;
        } else {
            throw new IllegalArgumentException("idk, idc");
        }
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('employee:read')")
    public ListResponse createList(@RequestBody ListRequest listRequest) {
        ListResponse listResponse = webClientBuilder.build()
                .post()
                .uri("http://list-service/api/v1/list/user",
                        uriBuilder -> uriBuilder.path("/" + jwtAuthenticationFilter.getUser()
                                        .getId())
                                .build())
                .header("userId", jwtAuthenticationFilter.getUser()
                        .getId())
                .body(BodyInserters.fromValue(listRequest))
                .retrieve()
                .bodyToMono(ListResponse.class)
                .block();

        return listResponse;
    }

    @DeleteMapping("/{listId}")
    @PreAuthorize("hasAuthority('employee:delete')")
    public ResponseEntity deleteList(@PathVariable Integer listId) {
        try {
            ResponseEntity response = webClientBuilder.build()
                    .delete()
                    .uri("http://list-service/api/v1/list/" + listId + "/user/" + jwtAuthenticationFilter.getUser()
                            .getId())
                    .retrieve()
                    .bodyToMono(ResponseEntity.class)
                    .block();

            return response;
        } catch (WebClientResponseException.NotFound ex) {
            String errorMessage = "List With id: " + listId + " not found.";
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(errorMessage);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Something went wrong. Please try again later.");
        }
    }

}


