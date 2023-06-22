package com.project.todo.controllers;

import com.project.todo.models.Item;
import com.project.todo.models.List;
import com.project.todo.models.UserModel;
import com.project.todo.service.ItemService;
import com.project.todo.service.ListService;
import com.project.todo.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ItemController {

    private ListService listService;
    private ItemService itemService;
    private UserService userService;

    public ItemController(ListService listService, ItemService itemService, UserService userService){
        this.listService = listService;
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping("api/list/{list_id}/item")
    public ResponseEntity<Item> createItem(@RequestBody Item item, @PathVariable("list_id") Long list_id){
        List list = this.listService.findById(list_id);
        if(list != null){
            UserModel user = getCurrentUser();
            if(list.getUser().getId().equals(user.getId())){
                Item newItem = new Item();
                newItem.setList(list);
                newItem.setTitle(item.getTitle());
                newItem.setDescription(item.getDescription());
                this.itemService.saveItem(newItem);
                return new ResponseEntity<Item>(newItem, HttpStatus.OK);
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("api/list/{list_id}/item")
    public ResponseEntity<java.util.List<Item>> getItems(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @PathVariable("list_id") Long list_id){
        List list = this.listService.findById(list_id);
        if(list != null){
            UserModel user = getCurrentUser();
            if(list.getUser().getId().equals(user.getId())){
                Page<Item> items = this.itemService.getItemsByListId(list_id, PageRequest.of(page, size));
                return ResponseEntity.ok(items.getContent());
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("api/list/{list_id}/item/{id}")
    public ResponseEntity<Item> getItem(@PathVariable("list_id") Long list_id, @PathVariable("id") Long id){
        List list = this.listService.findById(list_id);
        if(list != null){
            UserModel user = getCurrentUser();
            if(list.getUser().getId().equals(user.getId())){
                Item item = this.itemService.findById(id);
                return new ResponseEntity<Item>(item, HttpStatus.OK);
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("api/list/{list_id}/item/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable("list_id") Long list_id,
                                           @PathVariable("id") Long id,
                                           @RequestBody Item newItem){
        List list = this.listService.findById(list_id);
        if(list != null){
            UserModel user = getCurrentUser();
            if(list.getUser().getId().equals(user.getId())){
                Item item = this.itemService.findById(id);
                item.setTitle(newItem.getTitle());
                item.setDescription(newItem.getDescription());
                item.setIsDone(newItem.getIsDone());
                this.itemService.saveItem(item);
                return new ResponseEntity<Item>(item, HttpStatus.OK);
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("api/list/{list_id}/item/{id}")
    public ResponseEntity<HttpStatus> deleteItem(@PathVariable("list_id") Long list_id,
                                                 @PathVariable("id") Long id){
        List list = this.listService.findById(list_id);
        if(list != null){
            UserModel user = getCurrentUser();
            if(list.getUser().getId().equals(user.getId())){
                Item item = this.itemService.findById(id);
                this.itemService.deleteItem(item);
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
