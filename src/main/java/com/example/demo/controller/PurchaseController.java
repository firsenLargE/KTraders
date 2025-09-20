package com.example.demo.controller;

import com.example.demo.entity.Purchase;
import com.example.demo.service.PurchaseService;
import com.example.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/purchases")
public class PurchaseController {
    
    @Autowired
    private PurchaseService purchaseService;
    
    @Autowired
    private ProductService productService;

    @GetMapping
    public String listPurchases(@RequestParam(required = false) String supplier,
                               @RequestParam(defaultValue = "supplier") String sortBy,
                               @RequestParam(defaultValue = "asc") String sortOrder,
                               Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        
        List<Purchase> purchases;
        
        // Filter by supplier if specified
        if (supplier != null && !supplier.isEmpty() && !"all".equals(supplier)) {
            purchases = purchaseService.getPurchasesBySupplier(supplier);
        } else {
            purchases = purchaseService.getAllPurchases();
        }
        
        // Sort purchases based on the sortBy parameter
        if ("supplier".equals(sortBy)) {
            purchases.sort((p1, p2) -> {
                int comparison = p1.getSupplier().compareToIgnoreCase(p2.getSupplier());
                return "desc".equals(sortOrder) ? -comparison : comparison;
            });
        } else if ("date".equals(sortBy)) {
            purchases.sort((p1, p2) -> {
                int comparison = p1.getPurchaseDate().compareTo(p2.getPurchaseDate());
                return "desc".equals(sortOrder) ? -comparison : comparison;
            });
        } else if ("product".equals(sortBy)) {
            purchases.sort((p1, p2) -> {
                String product1 = p1.getProduct() != null ? p1.getProduct().getName() : "";
                String product2 = p2.getProduct() != null ? p2.getProduct().getName() : "";
                int comparison = product1.compareToIgnoreCase(product2);
                return "desc".equals(sortOrder) ? -comparison : comparison;
            });
        } else if ("totalCost".equals(sortBy)) {
            purchases.sort((p1, p2) -> {
                int comparison = p1.getTotalCost().compareTo(p2.getTotalCost());
                return "desc".equals(sortOrder) ? -comparison : comparison;
            });
        }
        
        // Get all unique suppliers for the dropdown
        List<String> allSuppliers = purchaseService.getAllPurchases().stream()
            .map(Purchase::getSupplier)
            .distinct()
            .sorted()
            .collect(java.util.stream.Collectors.toList());
        
        model.addAttribute("purchases", purchases);
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("allSuppliers", allSuppliers);
        model.addAttribute("selectedSupplier", supplier);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return "purchases";
    }

    @GetMapping("/add")
    public String showAddPurchaseForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("products", productService.getAllProducts());
        return "add-purchase";
    }

    @PostMapping("/add")
    public String addPurchase(@ModelAttribute Purchase purchase, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        
        try {
            if (purchase.getPurchaseDate() == null) {
                purchase.setPurchaseDate(LocalDate.now());
            }
            purchaseService.addPurchase(purchase);
            ra.addFlashAttribute("success", "Purchase added successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error adding purchase: " + e.getMessage());
        }
        return "redirect:/purchases";
    }

    @GetMapping("/edit/{id}")
    public String showEditPurchaseForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        
        Purchase purchase = purchaseService.getPurchaseById(id);
        if (purchase == null) return "redirect:/purchases";
        
        model.addAttribute("purchase", purchase);
        model.addAttribute("products", productService.getAllProducts());
        return "edit-purchase";
    }

    @PostMapping("/edit/{id}")
    public String updatePurchase(@PathVariable Long id, @ModelAttribute Purchase purchase, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        
        try {
            purchase.setId(id);
            purchaseService.updatePurchase(purchase);
            ra.addFlashAttribute("success", "Purchase updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error updating purchase: " + e.getMessage());
        }
        return "redirect:/purchases";
    }

    @GetMapping("/delete/{id}")
    public String deletePurchase(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        
        try {
            purchaseService.deletePurchase(id);
            ra.addFlashAttribute("success", "Purchase deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error deleting purchase: " + e.getMessage());
        }
        return "redirect:/purchases";
    }

    @GetMapping("/reports")
    public String showPurchaseReports(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        
        // Get current year and month data
        List<Purchase> yearlyPurchases = purchaseService.getPurchasesByYear(currentYear);
        List<Purchase> monthlyPurchases = purchaseService.getPurchasesByYearAndMonth(currentYear, currentMonth);
        
        BigDecimal yearlyTotal = purchaseService.getTotalPurchaseAmountByDateRange(
            LocalDate.of(currentYear, 1, 1), 
            LocalDate.of(currentYear, 12, 31)
        );
        
        BigDecimal monthlyTotal = purchaseService.getTotalPurchaseAmountByDateRange(
            LocalDate.of(currentYear, currentMonth, 1),
            LocalDate.of(currentYear, currentMonth, LocalDate.of(currentYear, currentMonth, 1).lengthOfMonth())
        );
        
        model.addAttribute("yearlyPurchases", yearlyPurchases);
        model.addAttribute("monthlyPurchases", monthlyPurchases);
        model.addAttribute("yearlyTotal", yearlyTotal);
        model.addAttribute("monthlyTotal", monthlyTotal);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        
        return "purchase-reports";
    }

    @GetMapping("/reports/yearly")
    public String yearlyReport(@RequestParam int year, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        
        List<Purchase> purchases = purchaseService.getPurchasesByYear(year);
        BigDecimal total = purchaseService.getTotalPurchaseAmountByDateRange(
            LocalDate.of(year, 1, 1), 
            LocalDate.of(year, 12, 31)
        );
        
        // Compute distinct suppliers and products
        List<String> distinctSuppliers = purchases.stream()
            .map(Purchase::getSupplier)
            .distinct()
            .collect(java.util.stream.Collectors.toList());
        
        List<String> distinctProducts = purchases.stream()
            .map(p -> p.getProduct() != null ? p.getProduct().getName() : "Unknown Product")
            .distinct()
            .collect(java.util.stream.Collectors.toList());
        
        model.addAttribute("purchases", purchases);
        model.addAttribute("total", total);
        model.addAttribute("year", year);
        model.addAttribute("distinctSuppliers", distinctSuppliers);
        model.addAttribute("distinctProducts", distinctProducts);
        
        return "yearly-purchase-report";
    }

    @GetMapping("/reports/monthly")
    public String monthlyReport(@RequestParam int year, @RequestParam int month, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        
        List<Purchase> purchases = purchaseService.getPurchasesByYearAndMonth(year, month);
        BigDecimal total = purchaseService.getTotalPurchaseAmountByDateRange(
            LocalDate.of(year, month, 1),
            LocalDate.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth())
        );
        
        // Compute distinct suppliers and products
        List<String> distinctSuppliers = purchases.stream()
            .map(Purchase::getSupplier)
            .distinct()
            .collect(java.util.stream.Collectors.toList());
        
        List<String> distinctProducts = purchases.stream()
            .map(p -> p.getProduct() != null ? p.getProduct().getName() : "Unknown Product")
            .distinct()
            .collect(java.util.stream.Collectors.toList());
        
        model.addAttribute("purchases", purchases);
        model.addAttribute("total", total);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("distinctSuppliers", distinctSuppliers);
        model.addAttribute("distinctProducts", distinctProducts);
        
        return "monthly-purchase-report";
    }

}
