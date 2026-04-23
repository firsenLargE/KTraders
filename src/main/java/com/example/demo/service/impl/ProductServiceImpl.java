package com.example.demo.service.impl;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        List<Product> products = productRepository.findAllByDeletedFalse();
        products.sort((p1, p2) -> {
            String name1 = p1.getName() != null ? p1.getName() : "";
            String name2 = p2.getName() != null ? p2.getName() : "";
            return name1.compareToIgnoreCase(name2);
        });
        return products;
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public void updateProduct(Product product) {
        productRepository.save(product);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.searchByNameOrCategory(query);
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

    @Transactional
    public void syncCapitalization() {
        productRepository.findAll().forEach(p -> {
            p.setName(p.getName());
            p.setCategory(p.getCategory());
            p.setSize(p.getSize());
            p.setSupplier(p.getSupplier());
            productRepository.save(p);
        });
    }
}
