package com.todo.taskservice.service;

import com.todo.taskservice.exception.ListNotFoundException;
import com.todo.taskservice.model.ListResponse;
import com.todo.taskservice.model.Task;
import com.todo.taskservice.model.TaskRequest;
import com.todo.taskservice.model.TaskResponse;
import com.todo.taskservice.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ManagementTaskService {

    private final TaskRepository taskRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;

    public List<TaskResponse> getAllTaskByTaskIdsArray(List<Integer> array) {
        List<Task> tasks = taskRepository.findAllByTaskIds(array);

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

    public TaskResponse createTaskForUser(String userId, Integer listId, TaskRequest request) throws ListNotFoundException {
        ListResponse listResponse = webClientBuilder.build()
                .get()
                .uri("http://list-service/api/v1/list/{listId}", listId)
                .header("userId", userId)
                .retrieve()
                .bodyToMono(ListResponse.class)
                .block();

        if (listResponse == null || !listResponse.getUserId()
                .equals(userId)) {
            throw new ListNotFoundException("List Not Found");
        }

        List<Integer> taskIds = listResponse.getTasksIds();
        if (taskIds == null) {
            taskIds = new ArrayList<>(); // Initialize the list if it's null
        }

        Task task = Task.builder()
                .name(request.getName())
                .listId(listId)
                .userId(userId)
                .description(request.getDescription())
                .completed(request.isCompleted())
                .createdAt(LocalDateTime.now())
                .build();

        Task newTask = taskRepository.saveAndFlush(task);

        taskIds.add(newTask.getId());

        ListResponse updatedList = webClientBuilder.build()
                .patch()
                .uri("http://list-service/api/v1/management/list/user/{userId}/list/{listId}", userId, listId)
                .bodyValue(newTask.getId())
                .retrieve()
                .bodyToMono(ListResponse.class)
                .block();

        TaskResponse taskResponse = new TaskResponse();
        BeanUtils.copyProperties(newTask, taskResponse);

        return taskResponse;
    }

    public Integer getTaskCount() {
        return Math.toIntExact(taskRepository.count());
    }
}
