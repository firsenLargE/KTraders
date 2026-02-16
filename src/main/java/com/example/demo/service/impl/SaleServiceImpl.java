package com.example.demo.service.impl;

import com.example.demo.entity.Sale;
import com.example.demo.repository.SaleRepository;
import com.example.demo.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class SaleServiceImpl implements SaleService {
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private com.example.demo.service.ProductService productService;
    @Autowired
    private com.example.demo.service.CashLedgerService cashLedgerService;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void addSale(Sale sale) {
        // Reduce product stock when sale is added
        com.example.demo.entity.Product product = sale.getProduct();
        if (product != null) {
            Integer currentStock = product.getQuantity() != null ? product.getQuantity() : 0;
            product.setQuantity(Math.max(0, currentStock - sale.getQuantity()));

            // Snapshot the cost price
            if (sale.getUnitCostPrice() == null) {
                sale.setUnitCostPrice(product.getActualPrice());
            }

            productService.updateProduct(product);
        }
        saleRepository.save(sale);

        // Record Cash Transaction
        com.example.demo.entity.CashTransaction tx = new com.example.demo.entity.CashTransaction();
        tx.setType(com.example.demo.entity.CashTransaction.Type.IN);
        tx.setAmount(java.math.BigDecimal.valueOf(sale.getTotalPrice()));
        tx.setPaymentMethod(com.example.demo.entity.CashTransaction.PaymentMethod.CASH);
        tx.setReference("SALE:" + sale.getId());
        tx.setCounterparty(sale.getCustomer() != null ? sale.getCustomer().getName() : "Walk-in Customer");
        tx.setOccurredAt(sale.getDate() != null ? sale.getDate().atStartOfDay() : java.time.LocalDateTime.now());
        tx.setNotes("Sale of " + (sale.getProduct() != null ? sale.getProduct().getName() : "Item"));
        cashLedgerService.record(tx);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void updateSale(Sale sale) {
        Sale existingSale = saleRepository.findById(sale.getId()).orElse(null);
        if (existingSale != null) {
            com.example.demo.entity.Product product = sale.getProduct();
            if (product != null) {
                // Adjust stock based on difference
                Integer qtyDiff = sale.getQuantity() - existingSale.getQuantity();
                Integer currentStock = product.getQuantity() != null ? product.getQuantity() : 0;
                product.setQuantity(Math.max(0, currentStock - qtyDiff));

                // If product changed or cost pricing was null, re-snapshot
                if (sale.getUnitCostPrice() == null) {
                    sale.setUnitCostPrice(product.getActualPrice());
                }

                productService.updateProduct(product);
            }
        }
        saleRepository.save(sale);

        // Update Cash Transaction
        cashLedgerService.deleteByReference("SALE:" + sale.getId());

        com.example.demo.entity.CashTransaction tx = new com.example.demo.entity.CashTransaction();
        tx.setType(com.example.demo.entity.CashTransaction.Type.IN);
        tx.setAmount(java.math.BigDecimal.valueOf(sale.getTotalPrice()));
        tx.setPaymentMethod(com.example.demo.entity.CashTransaction.PaymentMethod.CASH);
        tx.setReference("SALE:" + sale.getId());
        tx.setCounterparty(sale.getCustomer() != null ? sale.getCustomer().getName() : "Walk-in Customer");
        tx.setOccurredAt(sale.getDate() != null ? sale.getDate().atStartOfDay() : java.time.LocalDateTime.now());
        tx.setNotes("Sale of " + (sale.getProduct() != null ? sale.getProduct().getName() : "Item"));
        cashLedgerService.record(tx);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteSale(Integer id) {
        Sale sale = saleRepository.findById(id).orElse(null);
        if (sale != null) {
            // Restore product stock when sale is deleted
            com.example.demo.entity.Product product = sale.getProduct();
            if (product != null) {
                Integer currentStock = product.getQuantity() != null ? product.getQuantity() : 0;
                product.setQuantity(currentStock + sale.getQuantity());
                productService.updateProduct(product);
            }
        }
        cashLedgerService.deleteByReference("SALE:" + id);
        saleRepository.deleteById(id);
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public Sale getSaleById(Integer id) {
        return saleRepository.findById(id).orElse(null);
    }

    public List<Sale> getRecentSales(int days) {
        return saleRepository.findRecentSales(LocalDate.now().minusDays(days));
    }

    public List<Sale> getSalesByProductAndDateRange(Integer productId, LocalDate startDate, LocalDate endDate) {
        return saleRepository.findByProductAndDateRange(productId, startDate, endDate);
    }

    public Long getTotalSalesQuantityForProduct(Integer productId, LocalDate startDate, LocalDate endDate) {
        return saleRepository.sumQuantityByProductAndDateRange(productId, startDate, endDate).orElse(0L);
    }
}
