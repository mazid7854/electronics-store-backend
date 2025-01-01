package com.mazid.electronic.store.dataTransferObjects;

import com.mazid.electronic.store.entities.Order;
import com.mazid.electronic.store.entities.OrderItem;
import com.mazid.electronic.store.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Data
public class OrderDto {

    String orderId;



    private String orderStatus;
    private String paymentStatus;


    private int orderAmount;
    private Date orderDate;
    private String deliveryDate;
    private String deliveryAddress;
    private String BillingName;
    private String billingPhone;

    // userId
    private UserDto user;
    private List<OrderItemDto> orderItems= new ArrayList<>();
}
