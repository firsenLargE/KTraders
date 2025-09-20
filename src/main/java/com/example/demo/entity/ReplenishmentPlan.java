package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "replenishment_plans")
public class ReplenishmentPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer requestedBudget;
    private Integer totalCost;
    private Integer totalUnits;
    private String strategy;
    private LocalDate createdAt = LocalDate.now();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplenishmentLine> lines = new ArrayList<>();

    public Long getId() { return id; }
    public Integer getRequestedBudget() { return requestedBudget; }
    public void setRequestedBudget(Integer requestedBudget) { this.requestedBudget = requestedBudget; }
    public Integer getTotalCost() { return totalCost; }
    public void setTotalCost(Integer totalCost) { this.totalCost = totalCost; }
    public Integer getTotalUnits() { return totalUnits; }
    public void setTotalUnits(Integer totalUnits) { this.totalUnits = totalUnits; }
    public String getStrategy() { return strategy; }
    public void setStrategy(String strategy) { this.strategy = strategy; }
    public LocalDate getCreatedAt() { return createdAt; }
    public List<ReplenishmentLine> getLines() { return lines; }
    public void setLines(List<ReplenishmentLine> lines) { this.lines = lines; }
}


