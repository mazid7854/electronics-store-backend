package com.mazid.electronic.store.services;

import com.mazid.electronic.store.dataTransferObjects.ProductDto;
import com.mazid.electronic.store.utility.PageableResponse;


public interface ProductService {

    // create
       ProductDto create(ProductDto productDto);
    //update
    ProductDto update(ProductDto productDto, String productId);

    //delete
    void delete(String productId);

    //get single
    ProductDto getById(String productId);


    //get all
    PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);

    //get all : live
    PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir);


    //search product
    PageableResponse<ProductDto> search(String title, int pageNumber, int pageSize, String sortBy, String sortDir);

    // create product with category
    ProductDto createProductWithCategory(ProductDto productDto, String categoryId);

    // update category of product
    ProductDto updateCategoryOfProduct(String productId, String categoryId);

    // fetch product by category
    PageableResponse<ProductDto> getByCategory(String categoryId, int pageNumber, int pageSize, String sortBy, String sortDir);
}
