package com.example.demo.repository;

import com.example.demo.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Integer> {
    List<Sale> findByDate(LocalDate date);

    @Query("SELECT s FROM Sale s WHERE s.product.id = ?1 AND s.date BETWEEN ?2 AND ?3")
    List<Sale> findByProductAndDateRange(Integer productId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(s.quantity) FROM Sale s WHERE s.product.id = ?1 AND s.date BETWEEN ?2 AND ?3")
    Optional<Long> sumQuantityByProductAndDateRange(Integer productId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT s FROM Sale s WHERE s.date >= ?1 ORDER BY s.date DESC")
    List<Sale> findRecentSales(LocalDate fromDate);
}


