package com.mazid.electronic.store.dataTransferObjects;

import com.mazid.electronic.store.entities.Order;
import com.mazid.electronic.store.entities.Product;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderItemDto {

    private int orderItemId;
    private int quantity;
    private int totalPrice;


    private ProductDto product;



}
