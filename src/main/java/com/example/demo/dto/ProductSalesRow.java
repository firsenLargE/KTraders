package com.example.demo.dto;

public class ProductSalesRow {
    private Integer productId;
    private String productName;
    private java.time.LocalDate date;
    private Long qty;
    private Double totalRevenue; // Replacing net/gross for clarity

    // enriched
    private Double actualUnitCost;
    private Double profitLossAmount;

    public ProductSalesRow(Integer productId, String productName, java.time.LocalDate date, Long qty,
            Double totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.date = date;
        this.qty = qty;
        this.totalRevenue = totalRevenue;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
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

    public Double getActualUnitCost() {
        return actualUnitCost;
    }

    public void setActualUnitCost(Double actualUnitCost) {
        this.actualUnitCost = actualUnitCost;
    }

    public Double getProfitLossAmount() {
        return profitLossAmount;
    }

    public void setProfitLossAmount(Double profitLossAmount) {
        this.profitLossAmount = profitLossAmount;
    }
}
