package com.example.demo.dto;

import com.example.demo.enums.ReplenishmentStrategy;
import java.util.ArrayList;
import java.util.List;

public class PurchasePlanDTO {
    public int requestedBudget;
    public int totalCost;
    public int remaining;
    public int totalUnits;
    public ReplenishmentStrategy strategy;
    public List<PurchasePlanItemDTO> items = new ArrayList<>();
}


