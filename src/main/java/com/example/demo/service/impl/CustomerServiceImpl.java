package com.example.demo.service.impl;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired private CustomerRepository customerRepository;
    public void saveCustomer(Customer customer) { customerRepository.save(customer); }
    public List<Customer> getAllCustomers() { return customerRepository.findAll(); }
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
    }
    public void deleteCustomer(Long id) { customerRepository.deleteById(id); }
    public List<Customer> searchCustomers(String name) { return customerRepository.findByNameContainingIgnoreCase(name); }
}


