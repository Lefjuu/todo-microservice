package com.todo.listservice.controller;

import com.todo.listservice.model.ListLocal;
import com.todo.listservice.model.ListRequest;
import com.todo.listservice.model.ListResponse;
import com.todo.listservice.service.ListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
