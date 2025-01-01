package com.mazid.electronic.store.controllers;

import com.mazid.electronic.store.dataTransferObjects.OrderDto;
import com.mazid.electronic.store.services.OrderService;
import com.mazid.electronic.store.utility.ApiResponseMessage;
import com.mazid.electronic.store.utility.PageableResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/orders")
@Tag(name = "Order APIs",description = "Create, Get all orders, Get orders of user, Remove order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // create order
    @PostMapping("/create/{userId}")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto order, @PathVariable String userId){
        OrderDto orderDto = orderService.create(order, userId);
        return  new ResponseEntity<>(orderDto, HttpStatus.CREATED);

    }

    //remove order
    @DeleteMapping("/remove/{orderId}")
    public ResponseEntity<ApiResponseMessage> removeOrder(@PathVariable String orderId){
        orderService.remove(orderId);
        ApiResponseMessage message = ApiResponseMessage.builder().message("Order removed successfully !!").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    // get orders of user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrders(@PathVariable String userId){
        List<OrderDto> orders = orderService.findByUser(userId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // get all orders
    @GetMapping
    public ResponseEntity<PageableResponse<OrderDto>> getAllOrders(
            @RequestParam(value = "pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value = "pageSize",defaultValue = "10",required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "orderDate",required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc",required = false) String sortDir
    ){
        PageableResponse<OrderDto> orders = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
