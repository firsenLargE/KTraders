package com.example.demo.repository;

import com.example.demo.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    
    List<Purchase> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Purchase> findBySupplierContainingIgnoreCase(String supplier);
    
    List<Purchase> findByProductId(Integer productId);
    
    @Query("SELECT p FROM Purchase p WHERE YEAR(p.purchaseDate) = :year ORDER BY p.purchaseDate DESC")
    List<Purchase> findByYear(@Param("year") int year);
    
    @Query("SELECT p FROM Purchase p WHERE YEAR(p.purchaseDate) = :year AND MONTH(p.purchaseDate) = :month ORDER BY p.purchaseDate DESC")
    List<Purchase> findByYearAndMonth(@Param("year") int year, @Param("month") int month);
    
    @Query("SELECT SUM(p.totalCost) FROM Purchase p WHERE p.purchaseDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalPurchaseAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.purchaseDate BETWEEN :startDate AND :endDate")
    Long getPurchaseCountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
