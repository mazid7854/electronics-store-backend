package com.mazid.electronic.store.services.implementation;

import com.mazid.electronic.store.dataTransferObjects.CartDto;
import com.mazid.electronic.store.entities.Cart;
import com.mazid.electronic.store.entities.CartItem;
import com.mazid.electronic.store.entities.Product;
import com.mazid.electronic.store.entities.User;
import com.mazid.electronic.store.exceptions.BadApiRequestException;
import com.mazid.electronic.store.exceptions.ResourceNotFoundException;
import com.mazid.electronic.store.repositories.CartItemRepository;
import com.mazid.electronic.store.repositories.CartRepository;
import com.mazid.electronic.store.repositories.ProductRepository;
import com.mazid.electronic.store.repositories.UserRepository;
import com.mazid.electronic.store.services.CartService;
import com.mazid.electronic.store.utility.AddItemToCartRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CartServiceImplementation implements CartService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public CartDto addItemToCart(String userId, AddItemToCartRequest request) {

        int quantity = request.getQuantity();
        String productId = request.getProductId();

        if (quantity <= 0) {
            throw new BadApiRequestException("Requested quantity must be greater than 0 !!");
        }

        //fetch the product
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found in database !!"));
        //fetch the user from db
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found in database!!"));

        // remove password from response
        user.setPassword(null);

        Cart cart = null;
        try {
            cart = cartRepository.findByUser(user).get();
        } catch (NoSuchElementException e) {
            cart = new Cart();
            cart.setCartId(UUID.randomUUID().toString());
            cart.setCreatedAt(new Date());
            List<CartItem> items = cart.getItems();
            // calculate the total cart value
            double totalCartValue = items.stream().mapToDouble(CartItem::getTotalPrice).sum();

            // set the total cart value
            cart.setTotalCartValue((int) totalCartValue);
        }

        //perform cart operations
        //if cart items already present; then update
        AtomicReference<Boolean> updated = new AtomicReference<>(false);
        List<CartItem> items = cart.getItems();
        // calculate the total cart value
        double totalCartValue = items.stream().mapToDouble(CartItem::getTotalPrice).sum();


        // set the total cart value
        cart.setTotalCartValue((int) totalCartValue);

        Cart finalCart = cart;
        items = items.stream().peek(item -> {

            if (item.getProduct().getProductId().equals(productId)) {
                //item already present in cart
                item.setQuantity(quantity);
                item.setTotalPrice(quantity * product.getDiscountedPrice());
                finalCart.setTotalCartValue((int) totalCartValue);

                updated.set(true);
            }
        }).toList();



      // cart.setItems(updatedItems);

        //create items
        if (!updated.get()) {
            CartItem cartItem = CartItem.builder()
                    .quantity(quantity)
                    .totalPrice(quantity * product.getDiscountedPrice())
                    .cart(cart)
                    .product(product)
                    .build();
            finalCart.setTotalCartValue((int) totalCartValue);
            cart.getItems().add(cartItem);

        }


        cart.setUser(user);
        Cart updatedCart = cartRepository.save(cart);
        return mapper.map(updatedCart, CartDto.class);


    }

    @Override
    public void removeItemFromCart(String userId, int cartItemId) {
        // fetch cartItem from database
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("No cart item found with this id!"));
        cartItemRepository.delete(cartItem);

        // reduce the total cart value
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("No user found with this id!"));

        // fetch cart from database
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("No cart found with this user!"));


            cart.setTotalCartValue(cart.getTotalCartValue() - cartItem.getTotalPrice());



        cartRepository.save(cart);


    }

    @Override
    public void emptyCart(String userId) {
        // fetch user from database
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("No user found with this id!"));

        // fetch cart from database
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("No cart found with this user!"));

        cart.setTotalCartValue(0);

        // remove all cart items
        cart.getItems().clear();


        cartRepository.save(cart);

    }

    @Override
    public CartDto getCartByUser(String userId) {
        // fetch user from database
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("No user found with this id!"));

        //remove password from response
        user.setPassword(null);

        // fetch cart from database
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("No cart found with this user!"));
        if (cart.getItems().isEmpty()) {
            cart.setTotalCartValue(0);
//            throw new ResourceNotFoundException("Cart is empty!");
        }

        return mapper.map(cart, CartDto.class);

    }
}
