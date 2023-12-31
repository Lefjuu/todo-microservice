package com.todo.listservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.listservice.exception.ListNotFoundException;
import com.todo.listservice.model.ListLocal;
import com.todo.listservice.model.ListRequest;
import com.todo.listservice.model.ListResponse;
import com.todo.listservice.repository.ListRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ListService {

    @Autowired
    private ListRepository listRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;

    public ListResponse createList(String userId, ListRequest request) {
        ListLocal list = ListLocal.builder()
                .userId(userId)
                .name(request.getName())
                .taskIds(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .build();

        listRepository.saveAndFlush(list);

        ListResponse listResponse = new ListResponse();

        BeanUtils.copyProperties(list, listResponse);
        return listResponse;

    }

    public List<ListLocal> getAllUserList(String userId) {
        List<ListLocal> listLocals = listRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return listLocals;
    }

    public Optional<ListLocal> getListById(Integer listId, String userId) {
        Optional<ListLocal> list = listRepository.findById(listId);
        if (list.isEmpty() || !list.get()
                .getUserId()
                .equals(userId)) {
            throw new WebClientResponseException(HttpStatus.FORBIDDEN.value(), null, null, null, null);
        }

        return list;
    }

    @Transactional
    public ResponseEntity<Object> addTaskToList(Integer listId, Integer newTaskId) {
        try {
            ListLocal list = listRepository.findById(listId)
                    .orElseThrow(ListNotFoundException::new);
            List<Integer> currentTaskIds = list.getTaskIds();
            currentTaskIds.add(0, newTaskId);
            list.setTaskIds(currentTaskIds);
            listRepository.save(list);
            return ResponseEntity.ok(list);
        } catch (ListNotFoundException e) {
            return ResponseEntity.notFound()
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }


    public ResponseEntity deleteListWithTasks(Integer listId, String userId) throws ListNotFoundException {
        try {
            ListLocal list = listRepository.findById(listId)
                    .orElseThrow(ListNotFoundException::new);

            if (list == null || !list.getUserId()
                    .equals(userId)) {
                throw new IllegalArgumentException("List Not Found");
            }

            ResponseEntity deleteTasksResponse = webClientBuilder.build()
                    .patch()
                    .uri("http://task-service/api/v1/task/list/" + listId)
                    .bodyValue(list.getTaskIds())
                    .retrieve()
                    .toEntity(Void.class)
                    .block();

            listRepository.deleteById(listId);
            return ResponseEntity.ok()
                    .build();
        } catch (ListNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("List not found.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid list or user ID.");
        } catch (Exception ex) {
            log.error("An error occurred while deleting the list with tasks.", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error.");
        }
    }

    @Transactional
    public ResponseEntity deleteTaskIdFromList(Integer listId, Integer taskId) {
        Optional<ListLocal> optionalList = listRepository.findById(listId);

        if (optionalList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ListLocal list = optionalList.get();
        list.getTaskIds().remove(taskId);

        listRepository.save(list);

        return ResponseEntity.ok().build();
    }

}
