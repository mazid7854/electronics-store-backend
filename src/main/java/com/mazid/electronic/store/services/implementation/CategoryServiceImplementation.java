package com.mazid.electronic.store.services.implementation;

import com.mazid.electronic.store.dataTransferObjects.CategoryDto;
import com.mazid.electronic.store.entities.Category;
import com.mazid.electronic.store.exceptions.ResourceNotFoundException;
import com.mazid.electronic.store.repositories.CategoryRepository;
import com.mazid.electronic.store.services.CategoryService;
import com.mazid.electronic.store.utility.Helper;
import com.mazid.electronic.store.utility.PageableResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImplementation implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        //generating random category id
       String categoryId= UUID.randomUUID().toString();
       categoryDto.setCategoryId(categoryId);


        Category category = mapper.map(categoryDto, Category.class);
        //save category
        Category savedCategory = categoryRepository.save(category);
        return mapper.map(savedCategory,CategoryDto.class);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, String categoryId) {
        // get category of given id
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
        // updating category details
        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        category.setCoverImage(categoryDto.getCoverImage());

        //save category
        Category savedCategory = categoryRepository.save(category);

        return mapper.map(savedCategory,CategoryDto.class);
    }

    @Override
    public void delete(String categoryId) {

        // get category of given id
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("The category you are trying to delete does not exist!"));
        categoryRepository.delete(category);

    }

    @Override
    public PageableResponse<CategoryDto> getAll(int pageNumber,int pageSize,String sortBy,String sortDir) {

        Sort sort=(sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()) :(Sort.by(sortBy).ascending()) ;

        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);
        Page<Category> page = categoryRepository.findAll(pageable);

        return Helper.getPageableResponse(page, CategoryDto.class);
    }

    @Override
    public CategoryDto getById(String categoryId) {
        // get category of given id
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category with ID " + categoryId + " not found!"));


        return mapper.map(category,CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findByTitleContaining(String keyword) {
        List<Category> categories = categoryRepository.findByTitleContaining(keyword);
        if (categories.isEmpty()) {
            throw new ResourceNotFoundException("No categories found!");
        }



        return categories.stream()
                .map(cat -> mapper.map(cat, CategoryDto.class))
                .collect(Collectors.toList());




    }


}
