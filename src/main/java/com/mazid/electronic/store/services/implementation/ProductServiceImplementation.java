package com.mazid.electronic.store.services.implementation;

import com.mazid.electronic.store.dataTransferObjects.ProductDto;
import com.mazid.electronic.store.entities.Category;
import com.mazid.electronic.store.entities.Product;
import com.mazid.electronic.store.exceptions.ResourceNotFoundException;
import com.mazid.electronic.store.repositories.CategoryRepository;
import com.mazid.electronic.store.repositories.ProductRepository;
import com.mazid.electronic.store.services.ProductService;
import com.mazid.electronic.store.utility.Helper;
import com.mazid.electronic.store.utility.PageableResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class ProductServiceImplementation implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ProductDto create(ProductDto productDto) {
        // generate random id for product
        String productId = UUID.randomUUID().toString();
        productDto.setProductId(productId);

        //setting current date
        productDto.setAddedDate(new Date());

        Product product = mapper.map(productDto, Product.class);
        Product savedProduct = productRepository.save(product);
        return mapper.map(savedProduct,ProductDto.class);

    }

    @Override
    public ProductDto update(ProductDto productDto, String productId) {
        // fetch product from database
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("No product fount with this id!"));
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setStock(productDto.isStock());
        product.setLive(productDto.isLive());
       // product.setRating(productDto.getRating());
       // product.setProductReviews(productDto.getProductReviews());
        product.setProductImage(productDto.getProductImage());

        Product updatedProduct = productRepository.save(product);


        return mapper.map(updatedProduct,ProductDto.class);
    }

    @Override
    public void delete(String productId) {
        // fetch product from database
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("No product fount with this id!"));
        productRepository.delete(product);


    }

    @Override
    public ProductDto getById(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("No product fount with this id!"));
        return mapper.map(product,ProductDto.class);
    }



    @Override
    public PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);

        Page<Product> page = productRepository.findAll(pageable);


        return Helper.getPageableResponse(page, ProductDto.class);
        // return productRepository.findAll().stream().map(product -> mapper.map(product, ProductDto.class)).toList();
    }

    @Override
    public PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);

        Page<Product> page = productRepository.findByLiveTrue(pageable);


        return Helper.getPageableResponse(page, ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> search(String title, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);

        Page<Product> page = productRepository.findByTitleContaining(title,pageable);


        return Helper.getPageableResponse(page, ProductDto.class);
    }

    @Override
    public ProductDto createProductWithCategory(ProductDto productDto, String categoryId) {
        // fetch category from database
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("No category fount with this id!"));

        // generate random id for product
        String productId = UUID.randomUUID().toString();
        productDto.setProductId(productId);

        //setting current date
        productDto.setAddedDate(new Date());

        Product product = mapper.map(productDto, Product.class);
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);


        return mapper.map(savedProduct,ProductDto.class);
    }

    @Override
    public ProductDto updateCategoryOfProduct(String productId, String categoryId) {
        // fetch product from database
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("No product fount with this id!"));

        // fetch category from database
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("No category fount with this id!"));

        product.setCategory(category);

        Product saved = productRepository.save(product);
        return mapper.map(saved,ProductDto.class);
    }

    @Override
    public PageableResponse<ProductDto> getByCategory(String categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);

        // fetch category from database
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("No category fount with this id!"));

        Page<Product> page = productRepository.findByCategory( category, pageable);
        return Helper.getPageableResponse(page, ProductDto.class);
    }
}
