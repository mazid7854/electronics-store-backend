package com.mazid.electronic.store.dataTransferObjects;


import com.mazid.electronic.store.entities.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductDto {


    private String productId;


    private String title;

    private String description;


    private int price;



    private int discountedPrice;


    private int quantity;
    private Date addedDate;
    private boolean live;

    private boolean stock;

   // private int rating;

    private String productImage;

    private CategoryDto category;


}
