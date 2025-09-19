package com.example.demo.dto;

public class ProductSalesRow {
    private Integer productId;
    private String  productName;
    private Long    qty;
    private Double  net;
    private Double  vat;
    private Double  gross;

    // enriched
    private Double  actualUnitCost;
    private Double  profitLoss;

    public ProductSalesRow(Integer productId, String productName, Long qty, Double net, Double vat, Double gross) {
        this.productId = productId; this.productName = productName; this.qty = qty;
        this.net = net; this.vat = vat; this.gross = gross;
    }

    public Integer getProductId() { return productId; }
    public String  getProductName() { return productName; }
    public Long    getQty() { return qty; }
    public Double  getNet() { return net; }
    public Double  getVat() { return vat; }
    public Double  getGross() { return gross; }

    public Double getActualUnitCost() { return actualUnitCost; }
    public void setActualUnitCost(Double actualUnitCost) { this.actualUnitCost = actualUnitCost; }

    public Double getProfitLoss() { return profitLoss; }
    public void setProfitLoss(Double profitLoss) { this.profitLoss = profitLoss; }
}


