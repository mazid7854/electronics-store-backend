package com.mazid.electronic.store.entities;

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
@Entity
@Table(name = "orders")
public class Order {
    @Id
    String orderId;
    private String orderStatus;
    private String paymentStatus;



    private int orderAmount;
    private Date orderDate;
    private String deliveryDate;
    @Column(length = 1000)
    private String deliveryAddress;
    private String BillingName;
    private String billingPhone;

    // userId
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private  User user;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "order",orphanRemoval = true)
    private List<OrderItem> orderItems= new ArrayList<>();

}
