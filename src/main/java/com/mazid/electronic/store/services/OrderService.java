package com.mazid.electronic.store.services;

import com.mazid.electronic.store.dataTransferObjects.OrderDto;
import com.mazid.electronic.store.utility.PageableResponse;

import java.util.List;

public interface OrderService
{

    // create order
    OrderDto create(OrderDto orderDto, String userId);

    // remove order
    void remove(String orderId);

    // get orders of user

    List<OrderDto> findByUser(String userId);

    // get all orders
    PageableResponse<OrderDto> getAllOrders(int pageNumber, int pageSize, String sortBy, String sortDir);

    // get single

    // update

}
