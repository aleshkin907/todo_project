package com.project.todo.repository;

import com.project.todo.models.List;
import com.project.todo.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListRepository extends JpaRepository<List, Long> {
    Page<List> findByUserId(Long user_id, Pageable pageable);
}
