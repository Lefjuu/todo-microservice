package com.todo.apigateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.todo.apigateway.model.ListResponse;
import com.todo.apigateway.model.StatsResponse;
import com.todo.apigateway.user.User;
import com.todo.apigateway.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagementService {

    @Autowired
    private final WebClient.Builder webClientBuilder;

    @Autowired
    private UserRepository userRepository;
    public List<User> getAllEmployees() {
        return userRepository.findAllByRole("EMPLOYEE");
    }

    public StatsResponse getStats() {
        Integer employeeCount = userRepository.countByRole("EMPLOYEE");

        Object listCount = webClientBuilder.build()
                .get()
                .uri("http://list-service/api/v1/management/list/list-count")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> response.get("count").asInt())
                .block();

        Object taskCount = webClientBuilder.build()
                .get()
                .uri("http://task-service/api/v1/management/task/task-count")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> response.get("count").asInt())
                .block();



//        log.info(String.valueOf(employeeCount));
        StatsResponse statsResponse = new StatsResponse();
        statsResponse.setEmployeeCount(employeeCount);
        statsResponse.setListCount((Integer) listCount);
        statsResponse.setTaskCount((Integer) taskCount);
        return statsResponse;
    }
}
