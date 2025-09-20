package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.enums.OrderStatus;
import java.util.List;

public interface OrderService {
    void createOrder(Order order);
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    void updateOrderStatus(Long orderId, OrderStatus status);
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOrdersByCustomer(Long customerId);
    long countOrdersByStatus(OrderStatus status);
    void deleteOrder(Long id);
}


