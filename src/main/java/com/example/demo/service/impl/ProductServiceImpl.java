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

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAllByDeletedFalse();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public void updateProduct(Product product) {
        productRepository.save(product);
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndDeletedFalse(name);
    }

    public void deleteProduct(Integer id) {
        productRepository.findById(id).ifPresent(p -> {
            p.setDeleted(true);
            productRepository.save(p);
        });
    }

    public long countProducts() {
        return productRepository.findAllByDeletedFalse().size();
    }

    public long countLowStockProducts() {
        return productRepository.countByQuantityLessThanAndDeletedFalse(10);
    }

    public double getTotalInventoryValue() {
        return productRepository.findAllByDeletedFalse().stream()
                .mapToDouble(p -> (p.getPrice() != null ? p.getPrice() : 0.0)
                        * (p.getQuantity() != null ? p.getQuantity() : 0))
                .sum();
    }

    public List<Product> getProductsNeedingReorder() {
        return productRepository.findProductsNeedingReorder();
    }
}
