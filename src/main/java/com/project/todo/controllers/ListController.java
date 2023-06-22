package com.project.todo.controllers;

import com.project.todo.models.List;
import com.project.todo.models.UserModel;
import com.project.todo.service.ListService;
import com.project.todo.service.UserService;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Controller
public class ListController {
    private ListService listService;
    private UserService userService;


    public ListController(ListService listService, UserService userService){
        this.listService = listService;
        this.userService = userService;
    }


    @PostMapping("api/list")
    public ResponseEntity<List> createList(@RequestBody List list){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserModel user = this.userService.findByUsername(username);
        List newList = new List();
        newList.setUser(user);
        newList.setTitle(list.getTitle());
        newList.setDescription(list.getDescription());
        this.listService.saveList(newList);
        return new ResponseEntity<List>(newList, HttpStatus.OK);
    }

    @GetMapping("api/list")
    public ResponseEntity<java.util.List<List>> getListsByCurrentUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        UserModel user = getCurrentUser();
        Page<List> lists = this.listService.getListsByUserId(user.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(lists.getContent());
    }

//    @GetMapping("api/list/{id}")
//    public ResponseEntity<?> getList(@PathVariable(value="id") Long id){
//        UserModel user = getCurrentUser();
//        Optional<List> l = this.listService.findById(id);
//        List list = l.orElse(new List());
//        if (list.getUser().getId().equals(user.getId())){
//            return new ResponseEntity<List>(list, HttpStatus.OK);
//        }else{
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("List not found");
//        }
//    }

    @GetMapping("api/list/{id}")
    public ResponseEntity<List> getListById(@PathVariable("id") Long id){
        List list = this.listService.findById(id);
        if (list != null){
            UserModel user = getCurrentUser();
            if(list.getUser().getId().equals(user.getId())){
                return new ResponseEntity<List>(list, HttpStatus.OK);
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("api/list/{id}")
    ResponseEntity<List> updateList(@PathVariable("id") Long id, @RequestBody List listRequest){
        List list = this.listService.findById(id);
        if(list!=null){
            UserModel user = getCurrentUser();
                if(list.getUser().getId().equals(user.getId())){
                    list.setTitle(listRequest.getTitle());
                    list.setDescription(listRequest.getDescription());
                    this.listService.saveList(list);
                    return new ResponseEntity<List>(list, HttpStatus.OK);
                }else{
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("api/list/{id}")
    public ResponseEntity<HttpStatus> deleteList(@PathVariable("id") Long id){
        List list = this.listService.findById(id);
        if(list != null){
            UserModel user = getCurrentUser();
            if(list.getUser().getId().equals(user.getId())){
                this.listService.deleteList(list);
                return new ResponseEntity<>(HttpStatus.OK);
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    private UserModel getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return this.userService.findByUsername(username);
    }
}

