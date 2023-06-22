package com.project.todo.service;

import com.project.todo.models.Item;
import com.project.todo.models.List;
import com.project.todo.repository.ItemRepository;
import com.project.todo.repository.ListRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
    private ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }


    public Page<Item> getItemsByListId(Long listId, Pageable pageable) {
        return this.itemRepository.findByListId(listId, pageable);
    }

    public void saveItem(Item item){
        this.itemRepository.save(item);
    }

    public Item findById(long id){
        return this.itemRepository.findById(id).orElse(null);
    }

    public void deleteItem(Item item){
        this.itemRepository.delete(item);
    }
}
