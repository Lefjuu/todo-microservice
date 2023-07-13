package com.todo.taskservice.service;

import com.todo.taskservice.exception.ListNotFoundException;
import com.todo.taskservice.model.*;
import com.todo.taskservice.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(TaskRequest request, Integer listId) throws ListNotFoundException {
        ListResponse listResponse = webClientBuilder.build()
                .get()
                .uri("http://list-service/api/v1/list/{listId}", listId)
                .retrieve()
                .bodyToMono(ListResponse.class)
                .block();
        log.info(String.valueOf(listResponse));
        log.info(listResponse.getUserId());
        log.info(request.getUserId());

        if (listResponse == null || !listResponse.getUserId().equals(request.getUserId())) {
            throw new IllegalArgumentException("List Not Found");
        }
        log.info("checkpoint 1");

        Task task = Task.builder()
                .name(request.getName())
                .listId(listId)
                .userId(request.getUserId())
                .description(request.getDescription())
                .isImportant(request.isImportant())
                .createdAt(LocalDateTime.now())
                .build();
        Task newTask = taskRepository.saveAndFlush(task);

        log.info("checkpoint 2");


//        Integer[] updatedArray;
//        if (listResponse.getTask() != null) {
//            List<Integer> updatedList = new ArrayList<>(Arrays.asList(listResponse.getTask()));
//            updatedList.add(newTask.getId());
//            updatedArray = updatedList.toArray(new Integer[0]);
//        } else {
//            updatedArray = new Integer[]{newTask.getId()};
//        }
//        log.info(Arrays.toString(updatedArray));

        log.info("checkpoint 3");
        log.info(String.valueOf(newTask.getId()));
        log.info(String.valueOf(newTask.getId()));
        log.info(String.valueOf(newTask.getId()));
        log.info(String.valueOf(newTask.getId()));
        ListResponse updatedList =  webClientBuilder.build()
                .patch()
                .uri("http://list-service/api/v1/list/{listId}", listId)
                .bodyValue(newTask.getId())
                .retrieve()
                .bodyToMono(ListResponse.class)
                .block();

        log.info(String.valueOf(updatedList));

        log.info("checkpoint 4");

        TaskResponse taskResponse = new TaskResponse();
        BeanUtils.copyProperties(newTask, taskResponse);

        log.info("checkpoint 5");
        return taskResponse;
    }


    public List<TaskResponse> getAllTaskByListId(Integer listId) {
        List<Task> tasks = taskRepository.findByListId(listId);

        return tasks.stream()
                .map(this::mapToTaskResponse)
                .toList();
    }

    private TaskResponse mapToTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .listId(task.getListId())
                .name(task.getName())
                .description(task.getDescription())
                .isImportant(task.isImportant())
                .createdAt(task.getCreatedAt())
                .build();
    }

    public ResponseEntity deleteAllTaskByListId(Integer listId) {

        taskRepository.deleteAllByListId(listId);
        return new ResponseEntity("All Task with list id: " + listId + " deleted", HttpStatus.OK);
    }

    public ResponseEntity deleteTaskById(Integer taskId, String userId) throws WebClientResponseException {
        Optional<Task> taskResponse = taskRepository.findById(taskId);
        if (!taskResponse.isEmpty() && taskResponse.get()
                .getUserId()
                .equals(userId)) {
            taskRepository.deleteById(taskId);
            return new ResponseEntity("Task With id: " + taskId + " deleted", HttpStatus.OK);
        } else {
            throw new WebClientResponseException(HttpStatus.BAD_GATEWAY.value(), "Task With id: " + taskId + " not found", null, null, null);
        }
    }
}
