package com.todo.listservice.service;

import com.todo.listservice.model.ListLocal;
import com.todo.listservice.model.ListRequest;
import com.todo.listservice.model.ListResponse;
import com.todo.listservice.repository.ListRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListService {

    @Autowired
    private ListRepository listRepository;

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
        return listRepository.findByUserId(userId);
    }
}
