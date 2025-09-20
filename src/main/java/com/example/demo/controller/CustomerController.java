package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/customers")
public class CustomerController {
    @Autowired private CustomerService customerService;

    @GetMapping
    public String listCustomers(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("customer", new Customer());
        return "customers";
    }

    @GetMapping("/add")
    public String showAddCustomer(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("customer", new Customer());
        return "customers";
    }

    @PostMapping("/add")
    public String saveOrUpdateCustomer(@ModelAttribute Customer customer, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try { customerService.saveCustomer(customer); ra.addFlashAttribute("success", customer.getId()==null?"Customer added!":"Customer updated!"); }
        catch (Exception e) { ra.addFlashAttribute("error", "Error: " + e.getMessage()); }
        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        Customer customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try { customerService.deleteCustomer(id); ra.addFlashAttribute("success", "Customer deleted!"); }
        catch (Exception e) { ra.addFlashAttribute("error", "Error: " + e.getMessage()); }
        return "redirect:/customers";
    }
}


