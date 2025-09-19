package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryContainingIgnoreCase(String category);
    long countByQuantityLessThan(int quantity);
    List<Product> findByQuantityLessThan(int quantity);
    @Query("SELECT p FROM Product p ORDER BY p.date DESC")
    List<Product> findAllOrderByDateDesc();
    List<Product> findByImagePathIsNotNull();
    List<Product> findByImagePathIsNull();
    @Query("SELECT p FROM Product p WHERE p.quantity <= p.minStockLevel")
    List<Product> findProductsNeedingReorder();
}


