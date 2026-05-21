package com.shopai.service;
import com.shopai.model.Category;
import com.shopai.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
@Service @RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repo;
    public List<Category> getAllActive()         { return repo.findByIsActiveTrueOrderByName(); }
    public Optional<Category> getById(Long id)  { return repo.findById(id); }
    public Optional<Category> getBySlug(String slug) { return repo.findBySlug(slug); }
    public Category save(Category c)             { return repo.save(c); }
}
