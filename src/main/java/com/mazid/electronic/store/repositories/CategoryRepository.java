package com.mazid.electronic.store.repositories;


import com.mazid.electronic.store.dataTransferObjects.CategoryDto;
import com.mazid.electronic.store.entities.Category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,String> {
    List<Category> findByTitleContaining(String keyword);

}
