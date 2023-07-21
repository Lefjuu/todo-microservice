package com.todo.listservice.controller;

import com.todo.listservice.model.ListLocal;
import com.todo.listservice.model.ListRequest;
import com.todo.listservice.model.ListResponse;
import com.todo.listservice.service.ManagementListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/management/list")
@Slf4j
public class ManagementListController {

    @Autowired
    private ManagementListService managementListService;

    @GetMapping("/user/{userId}")
    public List<ListLocal> getAllUserList(@PathVariable String userId) {
        return managementListService.getAllUserList(userId);
    }

    @PostMapping("/user/{userId}")
    public ListResponse createListForUser(@PathVariable String userId, @RequestBody ListRequest listRequest ) {
        return managementListService.createListForUser(userId, listRequest);
    }

    @PatchMapping("/user/{userId}/list/{listId}")
    public ResponseEntity<Object> updateList(@PathVariable String userId, @PathVariable Integer listId, @RequestBody Integer taskId ) {
        return managementListService.updateList(userId, listId, taskId);
    }

    @GetMapping("/list-count")
    public ResponseEntity<Map<String, Integer>> getListCount() {
        Integer listCount = managementListService.getListCount();
        Map<String, Integer> response = new HashMap<>();
        response.put("count", listCount);
        return ResponseEntity.ok(response);
    }
}
