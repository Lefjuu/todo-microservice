package com.todo.taskservice.service;

import com.todo.taskservice.model.Task;
import com.todo.taskservice.model.TaskRequest;
import com.todo.taskservice.model.TaskResponse;
import com.todo.taskservice.repository.TaskRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(TaskRequest request) {
        Task task = Task.builder()
                .name(request.getName())
                .listId(request.getListId())
                .description(request.getDescription())
                .isImportant(request.isImportant())
                .createdAt(LocalDateTime.now())
                .build();
        taskRepository.saveAndFlush(task);

        TaskResponse taskResponse = new TaskResponse();

        BeanUtils.copyProperties(task, taskResponse);
        return taskResponse;
    }

    public List<TaskResponse> getAllTaskByListId(Integer listId) {
        List<Task> tasks = taskRepository.findByListId(listId);

        return tasks.stream().map(this::mapToTaskResponse).toList();
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
}
