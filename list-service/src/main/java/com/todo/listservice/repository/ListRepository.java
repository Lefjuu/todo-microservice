package com.todo.listservice.repository;

import com.todo.listservice.model.ListLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListRepository extends JpaRepository<ListLocal, Integer> {
    List<ListLocal> findByUserId(String userId);
}
