package com.mazid.electronic.store.services;

import com.mazid.electronic.store.dataTransferObjects.CategoryDto;
import com.mazid.electronic.store.dataTransferObjects.ProductDto;
import com.mazid.electronic.store.exceptions.ResourceNotFoundException;
import com.mazid.electronic.store.utility.PageableResponse;

import java.util.List;

public interface CategoryService {

    //create
     CategoryDto create(CategoryDto categoryDto);

    //update
    CategoryDto update(CategoryDto categoryDto,String categoryId);

    //delete
    void delete(String categoryId);

    //get all
    PageableResponse<CategoryDto> getAll(int pageNumber,int pageSize,String sortBy,String sortDir);


    //get single category details
    CategoryDto getById(String categoryId);

    //search
    List<CategoryDto> findByTitleContaining(String keyword);


}




