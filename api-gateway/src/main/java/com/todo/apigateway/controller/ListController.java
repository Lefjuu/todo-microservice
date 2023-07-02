package com.todo.apigateway.controller;

import com.todo.apigateway.config.CustomUserDetails;
import com.todo.apigateway.config.JwtAuthenticationFilter;
import com.todo.apigateway.model.ListRequest;
import com.todo.apigateway.model.ListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/list")
@PreAuthorize("hasRole('EMPLOYEE')")
@Slf4j
public class ListController {

    @Autowired
    private final WebClient.Builder webClientBuilder;
    @Autowired
    private final UserDetailsService userDetailsService;
    @Autowired
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private final CustomUserDetails customUserDetails;

    @Autowired
    private final UserDetails userDetails;

    @GetMapping()
    @PreAuthorize("hasAuthority('employee:read')")
    public ListResponse[] getUserLists() {
        ListResponse[] listResponseArray = webClientBuilder.build()
                .get()
                .uri("http://list-service/api/v1/list/user",
                        uriBuilder -> uriBuilder.path("/" + jwtAuthenticationFilter.getUser().getId())
                                .build())
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
    @PreAuthorize("hasAuthority('employee:write')")
    public ListResponse createList(@RequestBody ListRequest listRequest) {
        ListResponse listResponse = webClientBuilder.build()
                .post()
                .uri("http://list-service/api/v1/list/user",
                        uriBuilder -> uriBuilder.path("/" + jwtAuthenticationFilter.getUser().getId())
                                .build())
                .body(BodyInserters.fromValue(listRequest))
                .retrieve()
                .bodyToMono(ListResponse.class)
                .block();

//        if (listResponseArray != null) {
//            return listResponseArray;
//        } else {
//            throw new IllegalArgumentException("idk, idc");
//        }
        return listResponse;
    }

}


