package com.mazid.electronic.store.controllers;

import com.mazid.electronic.store.dataTransferObjects.CartDto;
import com.mazid.electronic.store.services.CartService;
import com.mazid.electronic.store.utility.AddItemToCartRequest;
import com.mazid.electronic.store.utility.ApiResponseMessage;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@Tag(name = "Cart APIs", description = "Get cart details,Add Items to cart, remove item from cart, clear cart")
public class CartController {

    @Autowired
    private CartService cartService;
    // add item to cart
    @PostMapping("/{userId}")
    public ResponseEntity<CartDto> addItemToCart(@PathVariable String userId, @RequestBody AddItemToCartRequest request)
    {
        CartDto cartDto = cartService.addItemToCart(userId, request);
        return new  ResponseEntity<>(cartDto, HttpStatus.CREATED);

    }

    // remove item from cart
    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<ApiResponseMessage> removeItemFromCart(@PathVariable String userId, @PathVariable int cartItemId)
    {
        cartService.removeItemFromCart(userId, cartItemId);
        ApiResponseMessage message = ApiResponseMessage.builder().message("Item removed from cart successfully !!").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    // empty cart
    @DeleteMapping("clear/{userId}")
    public ResponseEntity<ApiResponseMessage> emptyCart(@PathVariable String userId)
    {
        cartService.emptyCart(userId);
        ApiResponseMessage message = ApiResponseMessage.builder().message("Cart empty successfully !!").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    // get cart details
    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCartByUser(@PathVariable String userId)
    {
        CartDto cartDto = cartService.getCartByUser(userId);
        return new ResponseEntity<>(cartDto,HttpStatus.OK);
    }
}
