package com.todo.taskservice.service;

import com.todo.taskservice.exception.ListNotFoundException;
import com.todo.taskservice.exception.TaskNotFoundException;
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

    public TaskResponse createTask(TaskRequest request, Integer listId, String userId) throws ListNotFoundException {
        ListResponse listResponse = webClientBuilder.build()
                .get()
                .uri("http://list-service/api/v1/list/{listId}", listId)
                .header("userId", userId)
                .retrieve()
                .bodyToMono(ListResponse.class)
                .block();

        if (listResponse == null || !listResponse.getUserId()
                .equals(request.getUserId())) {
            throw new IllegalArgumentException("List Not Found");
        }

        Task task = Task.builder()
                .name(request.getName())
                .listId(listId)
                .userId(request.getUserId())
                .description(request.getDescription())
                .completed(request.isCompleted())
                .createdAt(LocalDateTime.now())

                .build();
        Task newTask = taskRepository.saveAndFlush(task);

        ListResponse updatedList = webClientBuilder.build()
                .patch()
                .uri("http://list-service/api/v1/list/{listId}", listId)
                .bodyValue(newTask.getId())
                .retrieve()
                .bodyToMono(ListResponse.class)
                .block();

        TaskResponse taskResponse = new TaskResponse();
        BeanUtils.copyProperties(newTask, taskResponse);

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
                .completed(task.isCompleted())
                .createdAt(task.getCreatedAt())
                .build();
    }

    public ResponseEntity deleteAllTasksById(List<Integer> tasksArray) {

        taskRepository.deleteAllByIdIn(tasksArray);
        return new ResponseEntity("All Task with list id: " + tasksArray + " deleted", HttpStatus.OK);
    }

    public ResponseEntity deleteTaskById(Integer taskId, String userId) throws WebClientResponseException {
        Optional<Task> taskResponse = taskRepository.findById(taskId);
        if (!taskResponse.isEmpty() && taskResponse.get()
                .getUserId()
                .equals(userId)) {
            taskRepository.deleteById(taskId);

            ResponseEntity deleteTasksResponse = webClientBuilder.build()
                    .delete()
                    .uri("http://list-service/api/v1/list/" + taskResponse.get()
                            .getListId() + "/task/" + taskId)
                    .retrieve()
                    .toEntity(Void.class)
                    .block();

            return new ResponseEntity("Task With id: " + taskId + " deleted", HttpStatus.OK);
        } else {
            throw new WebClientResponseException(HttpStatus.FORBIDDEN.value(), null, null, null, null);
        }
    }

    public Optional<Task> getTaskById(Integer taskId) {
        return taskRepository.findById(taskId);
    }

    public TaskResponse updateTask(Integer taskId, TaskRequest requestBody, String userId) {

        Optional<Task> currentTask = taskRepository.findById(taskId);
        if (currentTask.isEmpty() || !currentTask.get()
                .getUserId()
                .equals(userId)) {
            throw new WebClientResponseException(HttpStatus.FORBIDDEN.value(), null, null, null, null);
        }

        Task taskToUpdate = currentTask.get();
        taskToUpdate.setName(requestBody.getName());
        taskToUpdate.setDescription(requestBody.getDescription());

        taskRepository.save(taskToUpdate);

        return convertToTaskResponse(taskToUpdate);
    }

    private TaskResponse convertToTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .userId(task.getUserId())
                .listId(task.getListId())
                .name(task.getName())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .createdAt(task.getCreatedAt())
                .build();
    }

    public TaskResponse setTaskReverseComplete(Integer taskId, String userId) throws TaskNotFoundException {
        Optional<Task> currentTask = taskRepository.findById(taskId);

        if (currentTask.isEmpty() || !currentTask.get()
                .getUserId()
                .equals(userId)) {
            throw new WebClientResponseException(HttpStatus.FORBIDDEN.value(), null, null, null, null);
        }

        Task taskToUpdate = currentTask.get();
        boolean currentCompletedValue = taskToUpdate.isCompleted();
        taskToUpdate.setCompleted(!currentCompletedValue);

        taskRepository.save(taskToUpdate);

        return convertToTaskResponse(taskToUpdate);
    }
}
