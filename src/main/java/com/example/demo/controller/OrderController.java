package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.enums.OrderStatus;
import com.example.demo.service.CustomerService;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller @RequestMapping("/orders")
public class OrderController {
    @Autowired private OrderService orderService;
    @Autowired private CustomerService customerService;
    @Autowired private ProductService productService;

    @GetMapping
    public String listOrders(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("order", new Order());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("pendingCount", orderService.countOrdersByStatus(OrderStatus.PENDING));
        model.addAttribute("processingCount", orderService.countOrdersByStatus(OrderStatus.PROCESSING));
        model.addAttribute("shippedCount", orderService.countOrdersByStatus(OrderStatus.SHIPPED));
        model.addAttribute("deliveredCount", orderService.countOrdersByStatus(OrderStatus.DELIVERED));
        return "orders";
    }

    @PostMapping("/add")
    public String createOrder(@ModelAttribute Order order, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            Customer customer = customerService.getCustomerById(order.getCustomer().getId());
            Product product = productService.getProductById(order.getProduct().getId());
            if (customer == null || product == null) { ra.addFlashAttribute("error", "Invalid customer or product"); return "redirect:/orders"; }
            order.setCustomer(customer);
            order.setProduct(product);
            order.setUnitPrice(product.getPrice());
            order.setOrderDate(LocalDate.now());
            orderService.createOrder(order);
            ra.addFlashAttribute("success", "Order created!");
        } catch (Exception e) { ra.addFlashAttribute("error", "Error: " + e.getMessage()); }
        return "redirect:/orders";
    }

    @PostMapping("/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try { orderService.updateOrderStatus(id, status); ra.addFlashAttribute("success", "Status updated!"); }
        catch (Exception e) { ra.addFlashAttribute("error", "Error: " + e.getMessage()); }
        return "redirect:/orders";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try { orderService.deleteOrder(id); ra.addFlashAttribute("success", "Order deleted!"); }
        catch (Exception e) { ra.addFlashAttribute("error", "Error: " + e.getMessage()); }
        return "redirect:/orders";
    }
}


