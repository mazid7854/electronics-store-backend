package com.mazid.electronic.store.controllers;

import com.mazid.electronic.store.dataTransferObjects.ProductDto;
import com.mazid.electronic.store.services.FileService;
import com.mazid.electronic.store.services.ProductService;
import com.mazid.electronic.store.utility.ApiResponseMessage;
import com.mazid.electronic.store.utility.ImageResponse;
import com.mazid.electronic.store.utility.PageableResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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

@RestController
@RequestMapping("/products")
@Tag(name = "Product APIs", description = "Create,create product with category,update category of product, update, delete,get single,get all products by category,get all products,get all live products,search product, upload product image, save product image")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;

    @Value("${product.image.path}")
    private String imagePath;

    //create

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto){
        ProductDto product = productService.create(productDto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    // create product with category
    @PostMapping("/category/{categoryId}")
    public ResponseEntity<ProductDto> createProductWithCategory(@RequestBody ProductDto productDto,@PathVariable String categoryId){
        ProductDto product = productService.createProductWithCategory(productDto, categoryId);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    // update category of product
    @PutMapping("/category/{productId}/{categoryId}")
    public ResponseEntity<ProductDto> updateCategoryOfProduct(@PathVariable String productId, @PathVariable String categoryId){
        ProductDto product = productService.updateCategoryOfProduct(productId, categoryId);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }

    //update
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String productId ,@RequestBody ProductDto productDto){
        ProductDto updated = productService.update(productDto, productId);
        return new ResponseEntity<>(updated,HttpStatus.OK);

    }


    //delete
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable String productId){

        productService.delete(productId);

        ApiResponseMessage message = ApiResponseMessage.builder().message("Product deleted successfully !!").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(message,HttpStatus.OK);

    }

    //get single
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String productId){

        ProductDto product = productService.getById(productId);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }

    //get all
    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>> getAllProducts(
            @RequestParam(value = "pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value = "pageSize",defaultValue = "10",required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "title",required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc",required = false) String sortDir)
    {
        PageableResponse<ProductDto> all = productService.getAll(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(all,HttpStatus.OK);
    }

    //get all by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageableResponse<ProductDto>> getByCategory(
            @PathVariable String categoryId,
            @RequestParam(value = "pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value = "pageSize",defaultValue = "10",required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "title",required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc",required = false) String sortDir

    ){
        PageableResponse<ProductDto> all = productService.getByCategory(categoryId,pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(all,HttpStatus.OK);
    }

    //get all : live
    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>> getAllLive(
            @RequestParam(value = "pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value = "pageSize",defaultValue = "10",required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "title",required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc",required = false) String sortDir)
    {
        PageableResponse<ProductDto> all = productService.getAllLive(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(all,HttpStatus.OK);
    }

    //search

    @GetMapping("/search/{keyword}")
    public ResponseEntity<PageableResponse<ProductDto>> searchProduct(
            @PathVariable String keyword,
            @RequestParam(value = "pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value = "pageSize",defaultValue = "10",required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "title",required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc",required = false) String sortDir)
    {
        PageableResponse<ProductDto> searched = productService.search(keyword, pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(searched,HttpStatus.OK);

    }

    // upload product image
    @PostMapping("image/{productId}")
    public ResponseEntity<ImageResponse> uploadProductImage(
            @RequestParam("productImage") MultipartFile productImage,
            @PathVariable("productId") String productId
            ) {
        ProductDto productDto = productService.getById(productId);
        String imageFileName = fileService.uploadFile(productImage, imagePath, productId);
        productDto.setProductImage(imageFileName);
        ProductDto updated = productService.update(productDto, productId);
        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(updated.getProductImage())
                .success(true)
                .message("Product image uploaded successfully!")
                .status(HttpStatus.CREATED).build();
        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

    // serve product image
    @GetMapping("/image/{productId}")
    public void serveProductImage(@PathVariable String productId, HttpServletResponse response) throws IOException {
        ProductDto productDto = productService.getById(productId);
        InputStream resource = fileService.getResource(imagePath, productDto.getProductImage());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }

    }
