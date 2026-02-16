package com.example.demo.dto;

public class ProductSalesRow {
    private Integer productId;
    private String productName;
    private String customerName;
    private java.time.LocalDate date;
    private Long qty;
    private Double totalRevenue; // Replacing net/gross for clarity

    // enriched
    private Double snapshottedUnitCost;
    private Double profitLossAmount;

    public ProductSalesRow(Integer productId, String productName, String customerName, java.time.LocalDate date,
            Long qty, Double totalRevenue, Double snapshottedUnitCost) {
        this.productId = productId;
        this.productName = productName;
        this.customerName = customerName;
        this.date = date;
        this.qty = qty;
        this.totalRevenue = totalRevenue;
        this.snapshottedUnitCost = snapshottedUnitCost;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public java.time.LocalDate getDate() {
        return date;
    }

    public Long getQty() {
        return qty;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public Double getSnapshottedUnitCost() {
        return snapshottedUnitCost;
    }

    public void setSnapshottedUnitCost(Double snapshottedUnitCost) {
        this.snapshottedUnitCost = snapshottedUnitCost;
    }

    public Double getProfitLossAmount() {
        return profitLossAmount;
    }

    public void setProfitLossAmount(Double profitLossAmount) {
        this.profitLossAmount = profitLossAmount;
    }
}
