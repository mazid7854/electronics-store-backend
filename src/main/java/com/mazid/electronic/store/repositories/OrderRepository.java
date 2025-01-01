package com.mazid.electronic.store.repositories;

import com.mazid.electronic.store.entities.Order;
import com.mazid.electronic.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,String> {
    List<Order> findByUser(User user);
}
