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
    @Autowired private SaleRepository saleRepository;
    public void addSale(Sale sale) { saleRepository.save(sale); }
    public List<Sale> getAllSales() { return saleRepository.findAll(); }
    public Sale getSaleById(Integer id) { return saleRepository.findById(id).orElse(null); }
    public List<Sale> getRecentSales(int days) { return saleRepository.findRecentSales(LocalDate.now().minusDays(days)); }
    public List<Sale> getSalesByProductAndDateRange(Integer productId, LocalDate startDate, LocalDate endDate) {
        return saleRepository.findByProductAndDateRange(productId, startDate, endDate);
    }
    public Long getTotalSalesQuantityForProduct(Integer productId, LocalDate startDate, LocalDate endDate) {
        return saleRepository.sumQuantityByProductAndDateRange(productId, startDate, endDate).orElse(0L);
    }
}


