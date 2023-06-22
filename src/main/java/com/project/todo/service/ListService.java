package com.project.todo.service;

import com.project.todo.models.List;
import com.project.todo.repository.ListRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ListService {
    private ListRepository listRepository;

    public ListService(ListRepository listRepository){
        this.listRepository = listRepository;
    }

    public Page<List> getListsByUserId(Long userId, Pageable pageable) {
        return this.listRepository.findByUserId(userId, pageable);
    }

    public void saveList(List todoList){

        this.listRepository.save(todoList);
    }

    public List findById(long id){

        return this.listRepository.findById(id).orElse(null);
    }

    public void deleteList(List todoList){

        this.listRepository.delete(todoList);
    }
}
