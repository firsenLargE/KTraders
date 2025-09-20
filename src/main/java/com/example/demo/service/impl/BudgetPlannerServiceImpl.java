package com.example.demo.service.impl;

import com.example.demo.dto.PurchasePlanDTO;
import com.example.demo.dto.PurchasePlanItemDTO;
import com.example.demo.entity.Product;
import com.example.demo.entity.ReplenishmentLine;
import com.example.demo.entity.ReplenishmentPlan;
import com.example.demo.enums.ReplenishmentStrategy;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReplenishmentPlanRepository;
import com.example.demo.service.BudgetPlannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetPlannerServiceImpl implements BudgetPlannerService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired(required = false)
    private ReplenishmentPlanRepository planRepo;

    private static final class Cand {
        Product p; int price; int qty; int minLvl; int maxLvl; int gapToMin; double catW; double priority;
    }

    @Override
    @Transactional
    public PurchasePlanDTO planPurchases(int budget,
            ReplenishmentStrategy strategy,
            boolean persistPlan,
            java.util.Set<Integer> includeProductIds) {
        PurchasePlanDTO out = new PurchasePlanDTO();
        out.requestedBudget = Math.max(0, budget);
        out.strategy = strategy;
        if (budget <= 0)
            return out;

        List<Product> all = productRepository.findAll();

        List<Product> eligible = all.stream()
                .filter(p -> p.getPrice() != null && p.getPrice() > 0)
                .filter(p -> {
                    Integer q = p.getQuantity();
                    Integer mx = p.getMaxStockLevel();
                    return mx == null || q == null || q < mx;
                })
                .collect(Collectors.toList());

        if (includeProductIds != null && !includeProductIds.isEmpty()) {
            eligible = eligible.stream()
                    .filter(p -> includeProductIds.contains(p.getId()))
                    .collect(Collectors.toList());
        }
        if (eligible.isEmpty())
            return out;

        List<Cand> scored = new ArrayList<>();
        for (Product p : eligible) {
            Cand c = new Cand();
            c.p = p;
            c.price = p.getPrice() == null ? 0 : p.getPrice().intValue();
            c.qty = Optional.ofNullable(p.getQuantity()).orElse(0);
            c.minLvl = Optional.ofNullable(p.getMinStockLevel()).orElse(0);
            c.maxLvl = Optional.ofNullable(p.getMaxStockLevel()).orElse(Integer.MAX_VALUE);
            c.gapToMin = Math.max(0, c.minLvl - c.qty);
            String cat = (p.getCategory() == null ? "" : p.getCategory().toLowerCase());
            c.catW = switch (cat) {
                case "electronics" -> 0.12;
                case "premium" -> 0.10;
                case "bestseller" -> 0.08;
                case "gadgets" -> 0.05;
                default -> 0.00;
            };
            scored.add(c);
        }

        Map<Integer, Integer> bought = switch (strategy) {
            case ROUND_ROBIN -> purchaseRoundRobin(scored, budget);
            case LOW_STOCK_FIRST -> purchaseLowStockFirst(scored, budget);
            case BEST_VALUE -> purchaseBestValue(scored, budget);
        };

        for (var e : bought.entrySet()) {
            Integer pid = e.getKey();
            int units = e.getValue();
            if (units <= 0)
                continue;
            Product p = all.stream().filter(pp -> Objects.equals(pp.getId(), pid)).findFirst().orElse(null);
            if (p == null)
                continue;
            int unitPrice = p.getPrice().intValue();
            out.items.add(new PurchasePlanItemDTO(pid, p.getName(), units, unitPrice));
        }
        out.totalCost = out.items.stream().mapToInt(i -> i.lineCost).sum();
        out.remaining = Math.max(0, budget - out.totalCost);
        out.totalUnits = out.items.stream().mapToInt(i -> i.units).sum();

        if (persistPlan && planRepo != null && !out.items.isEmpty()) {
            ReplenishmentPlan plan = new ReplenishmentPlan();
            plan.setRequestedBudget(budget);
            plan.setTotalCost(out.totalCost);
            plan.setTotalUnits(out.totalUnits);
            plan.setStrategy(strategy.name());

            List<ReplenishmentLine> lines = new ArrayList<>();
            for (PurchasePlanItemDTO dto : out.items) {
                Product p = all.stream().filter(pp -> Objects.equals(pp.getId(), dto.productId)).findFirst()
                        .orElse(null);
                if (p == null)
                    continue;
                ReplenishmentLine line = new ReplenishmentLine();
                line.setPlan(plan);
                line.setProduct(p);
                line.setUnits(dto.units);
                line.setUnitPrice(dto.unitPrice);
                line.setLineCost(dto.lineCost);
                lines.add(line);
            }
            plan.setLines(lines);
            planRepo.save(plan);
        }

        return out;
    }

    private Map<Integer, Integer> purchaseRoundRobin(List<Cand> scored, int budget) {
        scored.forEach(c -> c.priority = (c.gapToMin + 100 * c.catW + 1.0) / Math.max(1, c.price));
        scored.sort((a, b) -> {
            int cmp = Double.compare(b.priority, a.priority);
            return (cmp != 0) ? cmp : Integer.compare(a.price, b.price);
        });

        Map<Integer, Integer> bought = new HashMap<>();
        int remaining = budget;
        boolean purchased;
        do {
            purchased = false;
            for (Cand c : scored) {
                int have = bought.getOrDefault(c.p.getId(), 0);
                int currentQty = c.qty + have;
                if (currentQty >= c.maxLvl)
                    continue;
                if (c.price > remaining)
                    continue;
                remaining -= c.price;
                bought.merge(c.p.getId(), 1, Integer::sum);
                purchased = true;
            }
        } while (purchased);
        return bought;
    }

    private Map<Integer, Integer> purchaseLowStockFirst(List<Cand> scored, int budget) {
        Map<Integer, Integer> bought = new HashMap<>();
        int remaining = budget;

        List<Cand> needingMin = scored.stream()
                .filter(c -> c.gapToMin > 0)
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.gapToMin, a.gapToMin);
                    return (cmp != 0) ? cmp : Integer.compare(a.price, b.price);
                })
                .toList();

        for (Cand c : needingMin) {
            while (remaining >= c.price) {
                int have = bought.getOrDefault(c.p.getId(), 0);
                int currentQty = c.qty + have;
                if (currentQty >= c.minLvl)
                    break;
                if (currentQty >= c.maxLvl)
                    break;
                remaining -= c.price;
                bought.merge(c.p.getId(), 1, Integer::sum);
            }
            if (remaining < 1)
                break;
        }
        if (remaining < 1)
            return bought;

        for (Cand c : needingMin) {
            while (remaining >= c.price) {
                int have = bought.getOrDefault(c.p.getId(), 0);
                int currentQty = c.qty + have;
                if (currentQty >= c.maxLvl)
                    break;
                remaining -= c.price;
                bought.merge(c.p.getId(), 1, Integer::sum);
            }
            if (remaining < 1)
                break;
        }
        return bought;
    }

    private Map<Integer, Integer> purchaseBestValue(List<Cand> scored, int budget) {
        Map<Integer, Integer> bought = new HashMap<>();
        int remaining = budget;
        if (remaining <= 0 || scored == null || scored.isEmpty())
            return bought;

        scored.forEach(c -> c.priority = (c.gapToMin + 100 * c.catW + 1.0) / Math.max(1, c.price));

        List<Cand> top3 = scored.stream()
                .filter(c -> c.qty < c.maxLvl)
                .sorted((a, b) -> {
                    int cmp = Double.compare(b.priority, a.priority);
                    return (cmp != 0) ? cmp : Integer.compare(a.price, b.price);
                })
                .limit(3)
                .toList();

        if (top3.isEmpty())
            return bought;

        while (remaining > 0) {
            boolean purchasedThisPass = false;
            int minTopPrice = top3.stream()
                    .filter(c -> (c.qty + bought.getOrDefault(c.p.getId(), 0)) < c.maxLvl)
                    .mapToInt(c -> c.price)
                    .min()
                    .orElse(Integer.MAX_VALUE);

            if (remaining < minTopPrice)
                break;

            for (Cand c : top3) {
                int have = bought.getOrDefault(c.p.getId(), 0);
                int currentQty = c.qty + have;
                if (currentQty >= c.maxLvl)
                    continue;
                if (remaining < c.price)
                    continue;
                remaining -= c.price;
                bought.merge(c.p.getId(), 1, Integer::sum);
                purchasedThisPass = true;
                if (remaining < minTopPrice)
                    break;
            }

            if (!purchasedThisPass)
                break;
        }

        return bought;
    }
}


