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

import java.util.*;

@Service
//@AllArgsConstructor
@Slf4j
public class ListService {

    @Autowired
    private ListRepository listRepository;


    @Autowired
    private ObjectMapper objectMapper;

    public ListResponse createList(String userId, ListRequest request) {
        ListLocal list = ListLocal.builder()
                .userId(userId)
                .name(request.getName())
                .build();

        listRepository.saveAndFlush(list);

        ListResponse listResponse = new ListResponse();

        BeanUtils.copyProperties(list, listResponse);
        return listResponse;

    }

    public List<ListLocal> getAllUserList(String userId) {
//        return listRepository.findByUserId(userId);
        List<ListLocal> listLocals = listRepository.findByUserId(userId);
        log.info(listLocals.toString());
        log.info(listLocals.toString());
        log.info(listLocals.toString());
        return  listLocals;
    }

    public Optional<ListLocal> getListById(Integer listId) {
        return listRepository.findById(listId);
    }

    @Transactional
    public ResponseEntity<Object> addTaskToList(Integer listId, Integer newTaskId) {
        try {
            ListLocal list = listRepository.findById(listId).orElseThrow(ListNotFoundException::new);
            List<Integer> currentTaskIds = list.getTaskIds();
            currentTaskIds.add(newTaskId);
            list.setTaskIds(currentTaskIds);
            log.info(String.valueOf(list));
            listRepository.save(list);
            return ResponseEntity.ok(list);
        } catch (ListNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
