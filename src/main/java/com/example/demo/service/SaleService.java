package com.example.demo.service;

import com.example.demo.entity.Sale;
import java.time.LocalDate;
import java.util.List;

public interface SaleService {
    void addSale(Sale sale);
    List<Sale> getAllSales();
    Sale getSaleById(Integer id);
    List<Sale> getRecentSales(int days);
    List<Sale> getSalesByProductAndDateRange(Integer productId, LocalDate startDate, LocalDate endDate);
    Long getTotalSalesQuantityForProduct(Integer productId, LocalDate startDate, LocalDate endDate);
}


