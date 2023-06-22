package com.project.todo.repository;

import com.project.todo.models.Item;
import com.project.todo.models.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByListId(Long listId, Pageable pageable);
}
