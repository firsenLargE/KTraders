package com.example.demo.dto;

public class PurchasePlanItemDTO {
    public Integer productId;
    public String productName;
    public int units;
    public int unitPrice;
    public int lineCost;

    public PurchasePlanItemDTO(Integer productId, String productName, int units, int unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.units = units;
        this.unitPrice = unitPrice;
        this.lineCost = units * unitPrice;
    }
}


