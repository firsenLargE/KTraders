package com.example.demo.util;

import com.example.demo.entity.Product;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class FinancialUtil {

    public static final double VAT_RATE = 0.13;

    public static double getCategoryWeight(String category) {
        if (category == null)
            return 0.0;
        return switch (category) {
            case "Bottlers Nepal" -> 0.15; // High priority beverage
            case "C.G. Brewery", "Nepal Ice Brewery" -> 0.12; // Alcohol/Premium
            case "Current Noodle", "Current Noodles" -> 0.10; // Bestsellers
            case "C.G. Foods And Industries" -> 0.08; // Diversified foods
            case "Dapcha Bhimsen", "Agro Thai Food" -> 0.05; // Specialties
            default -> 0.00;
        };
    }

    /**
     * Standardized valuation formula used across the dashboard and optimizer.
     * Value = price * (1.0 + catFactor + 0.20 * stockFactor)
     */
    public static int calculateProductValue(Product p) {
        if (p == null)
            return 0;

        double price = p.getPrice() != null ? p.getPrice() : 0.0;
        int stock = p.getQuantity() != null ? p.getQuantity() : 0;

        double catFactor = getCategoryWeight(p.getCategory());
        double stockFactor = Math.min(Math.max(stock, 0), 100) / 100.0;

        double v = price * (1.0 + catFactor + 0.20 * stockFactor);
        return Math.max(1, (int) Math.round(v));
    }

    public static BigDecimal calculateVat(BigDecimal netAmount) {
        if (netAmount == null)
            return BigDecimal.ZERO;
        return netAmount.multiply(BigDecimal.valueOf(VAT_RATE)).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateGross(BigDecimal netAmount) {
        if (netAmount == null)
            return BigDecimal.ZERO;
        return netAmount.add(calculateVat(netAmount)).setScale(2, RoundingMode.HALF_UP);
    }
}
