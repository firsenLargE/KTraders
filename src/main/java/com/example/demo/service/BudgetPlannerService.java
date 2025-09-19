package com.example.demo.service;

import com.example.demo.dto.PurchasePlanDTO;
import com.example.demo.enums.ReplenishmentStrategy;
import java.util.Set;

public interface BudgetPlannerService {
    PurchasePlanDTO planPurchases(int budget,
            ReplenishmentStrategy strategy,
            boolean persistPlan,
            Set<Integer> includeProductIds);
}


