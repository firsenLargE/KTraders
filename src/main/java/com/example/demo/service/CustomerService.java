package com.example.demo.service;

import com.example.demo.entity.Customer;
import java.util.List;

public interface CustomerService {
    void saveCustomer(Customer customer);
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    void deleteCustomer(Long id);
    List<Customer> searchCustomers(String name);
}


