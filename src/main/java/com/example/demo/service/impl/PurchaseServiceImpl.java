package com.example.demo.service.impl;

import com.example.demo.entity.Purchase;
import com.example.demo.entity.Product;
import com.example.demo.repository.PurchaseRepository;
import com.example.demo.service.PurchaseService;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    
    @Autowired
    private PurchaseRepository purchaseRepository;
    
    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public void addPurchase(Purchase purchase) {
        // Update product stock when adding purchase
        Product product = purchase.getProduct();
        if (product != null) {
            Integer currentStock = product.getQuantity() != null ? product.getQuantity() : 0;
            product.setQuantity(currentStock + purchase.getQuantity());
            productService.updateProduct(product);
        }
        purchaseRepository.save(purchase);
    }

    @Override
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    @Override
    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void updatePurchase(Purchase purchase) {
        Purchase existingPurchase = purchaseRepository.findById(purchase.getId()).orElse(null);
        if (existingPurchase != null) {
            // Adjust product stock if quantity changed
            Product product = purchase.getProduct();
            if (product != null && existingPurchase.getQuantity() != purchase.getQuantity()) {
                Integer stockDifference = purchase.getQuantity() - existingPurchase.getQuantity();
                Integer currentStock = product.getQuantity() != null ? product.getQuantity() : 0;
                product.setQuantity(currentStock + stockDifference);
                productService.updateProduct(product);
            }
        }
        purchaseRepository.save(purchase);
    }

    @Override
    @Transactional
    public void deletePurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id).orElse(null);
        if (purchase != null) {
            // Reduce product stock when deleting purchase
            Product product = purchase.getProduct();
            if (product != null) {
                Integer currentStock = product.getQuantity() != null ? product.getQuantity() : 0;
                product.setQuantity(Math.max(0, currentStock - purchase.getQuantity()));
                productService.updateProduct(product);
            }
        }
        purchaseRepository.deleteById(id);
    }

    @Override
    public List<Purchase> getPurchasesByDateRange(LocalDate startDate, LocalDate endDate) {
        return purchaseRepository.findByPurchaseDateBetween(startDate, endDate);
    }

    @Override
    public List<Purchase> getPurchasesByYear(int year) {
        return purchaseRepository.findByYear(year);
    }

    @Override
    public List<Purchase> getPurchasesByYearAndMonth(int year, int month) {
        return purchaseRepository.findByYearAndMonth(year, month);
    }

    @Override
    public List<Purchase> getPurchasesBySupplier(String supplier) {
        return purchaseRepository.findBySupplierContainingIgnoreCase(supplier);
    }

    @Override
    public List<Purchase> getPurchasesByProduct(Integer productId) {
        return purchaseRepository.findByProductId(productId);
    }

    @Override
    public BigDecimal getTotalPurchaseAmountByDateRange(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = purchaseRepository.getTotalPurchaseAmountByDateRange(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public Long getPurchaseCountByDateRange(LocalDate startDate, LocalDate endDate) {
        return purchaseRepository.getPurchaseCountByDateRange(startDate, endDate);
    }
}
