package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "replenishment_lines")
public class ReplenishmentLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private ReplenishmentPlan plan;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer units;
    private Integer unitPrice;
    private Integer lineCost;

    public Long getId() { return id; }
    public ReplenishmentPlan getPlan() { return plan; }
    public void setPlan(ReplenishmentPlan plan) { this.plan = plan; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getUnits() { return units; }
    public void setUnits(Integer units) { this.units = units; }
    public Integer getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Integer unitPrice) { this.unitPrice = unitPrice; }
    public Integer getLineCost() { return lineCost; }
    public void setLineCost(Integer lineCost) { this.lineCost = lineCost; }
}


