package com.example.demo.service.impl;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Autowired public ProductServiceImpl(ProductRepository productRepository) { this.productRepository = productRepository; }
    public void addProduct(Product product) { productRepository.save(product); }
    public List<Product> getAllProducts() { return productRepository.findAll(); }
    public Product getProductById(Integer id) { return productRepository.findById(id).orElse(null); }
    public void updateProduct(Product product) { productRepository.save(product); }
    public List<Product> searchProducts(String name) { return productRepository.findByNameContainingIgnoreCase(name); }
    public void deleteProduct(Integer id) { productRepository.findById(id).ifPresent(productRepository::delete); }
    public long countProducts() { return productRepository.count(); }
    public long countLowStockProducts() { return productRepository.countByQuantityLessThan(10); }
    public double getTotalInventoryValue() {
        return productRepository.findAll().stream()
            .mapToDouble(p -> (p.getPrice() != null ? p.getPrice() : 0.0) * (p.getQuantity() != null ? p.getQuantity() : 0))
            .sum();
    }
    public List<Product> getProductsNeedingReorder() { return productRepository.findProductsNeedingReorder(); }
}


