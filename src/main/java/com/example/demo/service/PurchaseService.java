package com.example.demo.service;

import com.example.demo.entity.Purchase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PurchaseService {
    void addPurchase(Purchase purchase);
    List<Purchase> getAllPurchases();
    Purchase getPurchaseById(Long id);
    void updatePurchase(Purchase purchase);
    void deletePurchase(Long id);
    List<Purchase> getPurchasesByDateRange(LocalDate startDate, LocalDate endDate);
    List<Purchase> getPurchasesByYear(int year);
    List<Purchase> getPurchasesByYearAndMonth(int year, int month);
    List<Purchase> getPurchasesBySupplier(String supplier);
    List<Purchase> getPurchasesByProduct(Integer productId);
    BigDecimal getTotalPurchaseAmountByDateRange(LocalDate startDate, LocalDate endDate);
    Long getPurchaseCountByDateRange(LocalDate startDate, LocalDate endDate);
}
