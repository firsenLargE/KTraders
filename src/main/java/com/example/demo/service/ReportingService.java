package com.example.demo.service;

import com.example.demo.dto.ProductSalesRow;
import com.example.demo.entity.Product;
import com.example.demo.repository.ExtendedSaleRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportingService {
    private final ExtendedSaleRepository repo;
    private final ProductRepository productRepo;

    @Autowired
    public ReportingService(ExtendedSaleRepository repo, ProductRepository productRepo) {
        this.repo = repo; this.productRepo = productRepo;
    }

    public List<ProductSalesRow> daily(LocalDate date) {
        List<ProductSalesRow> rows = repo.aggregateByProduct(date, date.plusDays(1));
        enrichProfitLoss(rows);
        return rows;
    }

    public List<ProductSalesRow> monthly(YearMonth ym) {
        LocalDate start = ym.atDay(1);
        LocalDate endExclusive = ym.plusMonths(1).atDay(1);
        List<ProductSalesRow> rows = repo.aggregateByProduct(start, endExclusive);
        enrichProfitLoss(rows);
        return rows;
    }

    private void enrichProfitLoss(List<ProductSalesRow> rows) {
        if (rows == null || rows.isEmpty()) return;
        Map<Integer, Product> pm = productRepo.findAll().stream().collect(Collectors.toMap(Product::getId, p -> p));
        for (ProductSalesRow r : rows) {
            Product p = pm.get(r.getProductId());
            double unitCost = 0.0;
            if (p != null) {
                Double ap = p.getActualPrice();
                unitCost = (ap != null) ? ap : (p.getPrice() != null ? p.getPrice() : 0.0);
            }
            double net = (r.getNet() != null) ? r.getNet() : 0.0;
            long qty = (r.getQty() != null) ? r.getQty() : 0L;
            double profitLoss = net - (unitCost * qty);
            r.setActualUnitCost(unitCost);
            r.setProfitLoss(profitLoss);
        }
    }
}


