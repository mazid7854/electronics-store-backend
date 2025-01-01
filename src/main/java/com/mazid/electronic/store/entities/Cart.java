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
@Table(name = "cart")
public class Cart {
    @Id
    private String cartId;
    private Date createdAt;
    private int totalCartValue;

    @OneToOne
    private User user;

    // mapping cart-items

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "cart",orphanRemoval = true)
    private List<CartItem> items= new ArrayList<>();

}
