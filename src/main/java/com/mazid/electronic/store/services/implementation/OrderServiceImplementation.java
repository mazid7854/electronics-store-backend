package com.mazid.electronic.store.services.implementation;

import com.mazid.electronic.store.dataTransferObjects.OrderDto;
import com.mazid.electronic.store.entities.*;
import com.mazid.electronic.store.exceptions.ResourceNotFoundException;
import com.mazid.electronic.store.repositories.CartRepository;
import com.mazid.electronic.store.repositories.OrderItemRepository;
import com.mazid.electronic.store.repositories.OrderRepository;
import com.mazid.electronic.store.repositories.UserRepository;
import com.mazid.electronic.store.services.OrderService;
import com.mazid.electronic.store.utility.Helper;
import com.mazid.electronic.store.utility.PageableResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OrderServiceImplementation implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public OrderDto create(OrderDto orderDto, String userId) {

        // fetch user from database
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found to create order"));

        // fetch cart from database
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("No cart found with this user!"));
        List<CartItem> cartItems = cart.getItems();

        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty. Cannot create order.");
        }

        // create order
        Order order = Order.builder()
                .orderDate(new Date())
                .BillingName(orderDto.getBillingName())
                .billingPhone(orderDto.getBillingPhone())
                .orderId(UUID.randomUUID().toString())
                .deliveryAddress(orderDto.getDeliveryAddress())
                .deliveryDate(orderDto.getDeliveryDate())
                .orderStatus(orderDto.getOrderStatus())
                .paymentStatus(orderDto.getPaymentStatus())
                .user(user)
                .build();

        // order items amount
        AtomicReference<Integer> orderAmount= new AtomicReference<>(0);

        // cart items to order items
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {

            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrice(cartItem.getQuantity() * cartItem.getProduct().getDiscountedPrice())
                    .order(order)
                    .build();
                orderAmount.set(orderAmount.get()+orderItem.getTotalPrice());
            return orderItem;
        }).toList();

        order.setOrderItems(orderItems);
        order.setOrderAmount(orderAmount.get());

        // clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        // save order
        Order savedOrder = orderRepository.save(order);
        return mapper.map(savedOrder, OrderDto.class);

    }

    @Override
    public void remove(String orderId) {
        // fetch the order from database
        orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found with this id!"));
        orderRepository.deleteById(orderId);

    }

    @Override
    public List<OrderDto> findByUser(String userId) {
        // fetch user from database
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found to find orders"));

        // fetch orders from database
        List<Order> byUser = orderRepository.findByUser(user);

        if (!byUser.isEmpty()) {

            return byUser.stream().map(order -> mapper.map(order, OrderDto.class)).toList();
        }else {
            return List.of();
        }


    }

    @Override
    public PageableResponse<OrderDto> getAllOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);
        Page<Order> orderPage = orderRepository.findAll(pageable);

        return Helper.getPageableResponse(orderPage, OrderDto.class);
    }
}
