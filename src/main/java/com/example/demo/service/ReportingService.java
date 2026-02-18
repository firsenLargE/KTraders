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
    private final com.example.demo.repository.PurchaseRepository purchaseRepo;

    @Autowired
    public ReportingService(ExtendedSaleRepository repo, ProductRepository productRepo,
            com.example.demo.repository.PurchaseRepository purchaseRepo) {
        this.repo = repo;
        this.productRepo = productRepo;
        this.purchaseRepo = purchaseRepo;
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

    public List<ProductSalesRow> filter(LocalDate start, LocalDate end, Long customerId) {
        LocalDate s = (start != null) ? start : LocalDate.of(1900, 1, 1);
        LocalDate e = (end != null) ? end : LocalDate.of(2100, 12, 31);

        List<ProductSalesRow> rows;
        if (customerId != null) {
            rows = repo.filterSalesByCustomer(s, e, customerId);
        } else {
            rows = repo.filterAllSales(s, e);
        }
        enrichProfitLoss(rows);
        return rows;
    }

    private void enrichProfitLoss(List<ProductSalesRow> rows) {
        if (rows == null || rows.isEmpty())
            return;
        Map<Integer, Product> pm = productRepo.findAll().stream().collect(Collectors.toMap(Product::getId, p -> p));

        for (ProductSalesRow r : rows) {
            Double unitCost = r.getSnapshottedUnitCost(); // Highest priority: Snapshotted cost at time of sale

            // If no snapshot (legacy sales), fallback to product history/guesses
            if (unitCost == null || unitCost <= 0) {
                Product p = pm.get(r.getProductId());
                if (p != null) {
                    unitCost = p.getActualPrice();
                    if (unitCost == null || unitCost <= 0) {
                        // Fallback to latest purchase price
                        List<java.math.BigDecimal> history = purchaseRepo.findUnitCostByProductId(p.getId());
                        if (!history.isEmpty()) {
                            unitCost = history.get(0).doubleValue();
                            p.setActualPrice(unitCost);
                            productRepo.save(p);
                        } else if (p.getPrice() != null) {
                            // Safe default: 80% of selling price
                            unitCost = p.getPrice() * 0.8;
                        }
                    }
                }
            }

            if (unitCost == null)
                unitCost = 0.0;

            double revenue = (r.getTotalRevenue() != null) ? r.getTotalRevenue() : 0.0;
            long qty = (r.getQty() != null) ? r.getQty() : 0L;

            r.setSnapshottedUnitCost(unitCost);
            double profitLoss = revenue - (unitCost * qty);
            r.setProfitLossAmount(profitLoss);
        }
    }
}
