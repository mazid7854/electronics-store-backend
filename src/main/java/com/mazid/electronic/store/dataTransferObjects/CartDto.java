package com.mazid.electronic.store.dataTransferObjects;



import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDto {
    private String cartId;
    private Date createdAt;
    private int totalCartValue;
    private UserDto user;
    private List<CartItemDto> items= new ArrayList<>();

}
