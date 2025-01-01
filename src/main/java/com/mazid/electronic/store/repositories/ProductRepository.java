package com.mazid.electronic.store.repositories;


import com.mazid.electronic.store.entities.Category;
import com.mazid.electronic.store.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,String> {
    //Search
    Page<Product> findByTitleContaining(String title,Pageable pageable);
    Page<Product> findByLiveTrue(Pageable pageable);

    // finder method
    Page<Product> findByCategory(Category category, Pageable pageable);
}
