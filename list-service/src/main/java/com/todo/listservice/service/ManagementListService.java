package com.todo.listservice.service;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ManagementListService {

    @Autowired
    private ListRepository listRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;
    public List<ListLocal> getAllUserList(String userId) {
        List<ListLocal> listLocals = listRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return listLocals;
    }

    public ListResponse createListForUser(String userId, ListRequest listRequest) {
        ListLocal list = ListLocal.builder()
                .userId(userId)
                .name(listRequest.getName())
                .taskIds(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .build();

        listRepository.saveAndFlush(list);

        ListResponse listResponse = new ListResponse();

        BeanUtils.copyProperties(list, listResponse);
        return listResponse;
    }

    @Transactional
    public ResponseEntity<Object> updateList(String userId, Integer listId, Integer taskId) {
        try {
            ListLocal list = listRepository.findById(listId)
                    .orElseThrow(ListNotFoundException::new);
            List<Integer> currentTaskIds = list.getTaskIds();
            currentTaskIds.add(0, taskId);
            list.setTaskIds(currentTaskIds);
            if(!userId.equals(list.getUserId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build();
            }
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

    public Integer getListCount() {
        return Math.toIntExact(listRepository.count());
    }
}
