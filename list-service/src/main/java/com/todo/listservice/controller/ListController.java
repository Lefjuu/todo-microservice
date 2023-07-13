package com.todo.listservice.controller;

import com.todo.listservice.model.ListLocal;
import com.todo.listservice.model.ListRequest;
import com.todo.listservice.model.ListResponse;
import com.todo.listservice.service.ListService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/list")
@Slf4j
public class ListController {

    @Autowired
    private ListService listService;

    @PostMapping("/user/{userId}")
    public ListResponse createList(@PathVariable String userId, @RequestBody ListRequest listRequest) {
        return listService.createList(userId, listRequest);
    }

    @GetMapping("/user/{userId}")
    public List<ListLocal> getAllUserList(@PathVariable String userId) {
        log.info(userId);
        return listService.getAllUserList(userId);
    }

    @GetMapping("/{listId}")
    public Optional<ListLocal> getListById(@PathVariable Integer listId) {
        return listService.getListById(listId);
    }

    @PatchMapping("/{listId}")
    @Transactional
    public ResponseEntity<Object> addTaskToList(@PathVariable Integer listId, @RequestBody Integer taskId) {
        log.info(String.valueOf(listId));
        log.info(String.valueOf(taskId));
        return listService.addTaskToList(listId, taskId);
    }
}
