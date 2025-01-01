package com.mazid.electronic.store.controllers;

import com.mazid.electronic.store.dataTransferObjects.CategoryDto;
import com.mazid.electronic.store.dataTransferObjects.UserDto;
import com.mazid.electronic.store.exceptions.ResourceNotFoundException;
import com.mazid.electronic.store.services.CategoryService;
import com.mazid.electronic.store.services.FileService;
import com.mazid.electronic.store.services.UserService;
import com.mazid.electronic.store.utility.ApiResponseMessage;
import com.mazid.electronic.store.utility.ImageResponse;
import com.mazid.electronic.store.utility.PageableResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "Category APIs", description = "Create, Update, Delete, get all categories, get single category, upload category cover image, download category cover image, search categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileService fileService;

    @Value("${category.cover.image.path}")
    private String imagePathForCategory;

    Logger logger= LoggerFactory.getLogger(CategoryController.class);



    //create
    @PostMapping
   public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto){
        CategoryDto category = categoryService.create(categoryDto);
        return new ResponseEntity<>(category, HttpStatus.CREATED);

    }

    //update
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody CategoryDto categoryDto,
                                                      @PathVariable String categoryId)
    {
        CategoryDto updatedCategory = categoryService.update(categoryDto, categoryId);
        return new ResponseEntity<>(updatedCategory,HttpStatus.OK);
    }


    //delete
    @DeleteMapping("/{categoryId}")
     public ResponseEntity<ApiResponseMessage> deleteCategory(@PathVariable String categoryId){
        try {
            CategoryDto categoryById = categoryService.getById(categoryId);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("The category you are trying to delete is not found");

        }

        categoryService.delete(categoryId);
        ApiResponseMessage message= ApiResponseMessage.builder().message("Category deleted successfully!").status(HttpStatus.OK).success(true).build();
        return new ResponseEntity<>(message,HttpStatus.OK);
     }

    // get all
     @GetMapping
    public ResponseEntity<PageableResponse<CategoryDto>> getAllCategory(
            @RequestParam(value = "pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value = "pageSize",defaultValue = "1",required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "title",required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = "asc",required = false) String sortDir
    )
    {
        PageableResponse<CategoryDto> pageableResponse = categoryService.getAll(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse,HttpStatus.OK);
    }

    //get single by id
    @GetMapping("{categoryId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable String categoryId)
    {
        CategoryDto categoryById = categoryService.getById(categoryId);
        return ResponseEntity.ok(categoryById);
    }

    // upload cover image
    @PostMapping("image/{categoryId}")
    public ResponseEntity<ImageResponse> uploadCategoryCoverImage(
            @RequestParam("categoryCoverImage")MultipartFile image,
            @PathVariable("categoryId") String categoryId
            ){



        CategoryDto categoryById = categoryService.getById(categoryId);
        if (categoryById == null) {
            throw new ResourceNotFoundException("Category with ID " + categoryId + " not found!");
        }

        String uploadFileName = fileService.uploadFile(image,imagePathForCategory,categoryId);
        categoryById.setCoverImage(uploadFileName);

        CategoryDto updated = categoryService.update(categoryById, categoryId);
        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(uploadFileName)
                .message("Cover image uploaded successfully!")
                .success(true)
                .status(HttpStatus.CREATED).build();
         return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }

    //serve cover image
    @GetMapping("image/{categoryId}")
     public void serveCategoryCoverImage(@PathVariable String categoryId, ServletResponse response) throws IOException {

        CategoryDto categoryById = categoryService.getById(categoryId);

        String coverImageName = categoryById.getCoverImage();

        InputStream resource = fileService.getResource(imagePathForCategory, coverImageName);

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource,response.getOutputStream());

    }

    //search
    @GetMapping("search/{keyword}")
    public ResponseEntity<List<CategoryDto>>searchCategory(@PathVariable String keyword){
        return new ResponseEntity<>(categoryService.findByTitleContaining(keyword),HttpStatus.OK);

    }



}
