package com.todo.listservice.controller;

import com.todo.listservice.model.ListLocal;
import com.todo.listservice.model.ListRequest;
import com.todo.listservice.model.ListResponse;
import com.todo.listservice.service.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/list")
public class ListController {

    @Autowired
    private ListService listService;

    @PostMapping
    public ListResponse createList(@RequestBody ListRequest listRequest) {
        return listService.createList(listRequest);
    }

    @GetMapping("/user/{userId}")
    public List<ListLocal> getAllUserList(@PathVariable String userId) {
        return listService.getAllUserList(userId);
    }

}
