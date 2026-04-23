package com.example.demo.controller;

import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.stream.Collectors;

@Controller
public class CategoryController {

    @Autowired
    private ProductService productService;

    @GetMapping("/categories")
    public String listCategories(Model model) {
        var products = productService.getAllProducts();
        var categoryMap = products.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory() != null ? p.getCategory() : "Uncategorized",
                        java.util.TreeMap::new,
                        Collectors.counting()));

        var categoriesList = categoryMap.entrySet().stream()
                .map(e -> {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("name", e.getKey());
                    m.put("count", e.getValue());
                    return m;
                }).collect(Collectors.toList());

        model.addAttribute("categories", categoriesList);
        return "categories";
    }
}
