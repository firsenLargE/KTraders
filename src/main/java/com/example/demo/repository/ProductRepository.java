package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByNameContainingIgnoreCaseAndDeletedFalse(String name);

    List<Product> findByCategoryContainingIgnoreCaseAndDeletedFalse(String category);

    long countByQuantityLessThanAndDeletedFalse(int quantity);

    List<Product> findByQuantityLessThanAndDeletedFalse(int quantity);

    @Query("SELECT p FROM Product p WHERE p.deleted = false ORDER BY p.date DESC")
    List<Product> findAllActiveOrderByDateDesc();

    List<Product> findAllByDeletedFalse();

    List<Product> findByImagePathIsNotNull();

    List<Product> findByImagePathIsNull();

    @Query("SELECT p FROM Product p WHERE p.quantity <= p.minStockLevel AND p.deleted = false")
    List<Product> findProductsNeedingReorder();
}
