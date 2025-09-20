package com.example.demo.service.impl;

import com.example.demo.entity.Order;
import com.example.demo.enums.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired private OrderRepository orderRepository;
    public void createOrder(Order order) {
        if (order.getTotalAmount()==null && order.getUnitPrice()!=null && order.getQuantity()!=null) {
            order.setTotalAmount(order.getUnitPrice() * order.getQuantity());
        }
        orderRepository.save(order);
    }
    public List<Order> getAllOrders() { return orderRepository.findAllOrderByOrderDateDesc(); }
    public Order getOrderById(Long id) { return orderRepository.findById(id).orElse(null); }
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        orderRepository.findById(orderId).ifPresent(o -> { o.setStatus(status); orderRepository.save(o); });
    }
    public List<Order> getOrdersByStatus(OrderStatus status) { return orderRepository.findByStatus(status); }
    public List<Order> getOrdersByCustomer(Long customerId) { return orderRepository.findByCustomerId(customerId); }
    public long countOrdersByStatus(OrderStatus status) { return orderRepository.countByStatus(status); }
    public void deleteOrder(Long id) { orderRepository.deleteById(id); }
}


