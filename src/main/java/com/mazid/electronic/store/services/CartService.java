package com.mazid.electronic.store.services;

import com.mazid.electronic.store.dataTransferObjects.CartDto;
import com.mazid.electronic.store.utility.AddItemToCartRequest;

public interface CartService {

    // add items to cart
    //case1: if cart is not available for user create a new cart and then add items
    //case2: if cart is available for user then add items

    CartDto addItemToCart(String userId, AddItemToCartRequest request);

    // remove item from cart
    void removeItemFromCart(String userId, int cartItemId);


    // empty cart
    void emptyCart(String userId);

    // get cart details
    CartDto getCartByUser(String userId);
}
