package com.example.demo.service;

import com.example.demo.entity.Product;
import java.util.List;

public interface ProductService {
    void addProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(Integer id);
    void updateProduct(Product product);
    List<Product> searchProducts(String name);
    void deleteProduct(Integer id);
    long countProducts();
    long countLowStockProducts();
    double getTotalInventoryValue();
    List<Product> getProductsNeedingReorder();
}


