package com.example.demo.controller;

import com.example.demo.entity.StockMovement;
import com.example.demo.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventory/movements")
public class StockMovementController {
    private final StockMovementService svc;

    @Autowired
    StockMovementController(StockMovementService svc) {
        this.svc = svc;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("rows", svc.all());
        model.addAttribute("currentUri", "/inventory/movements");
        return "stock_in_out";
    }

    @GetMapping("/{productId}")
    public String view(@PathVariable Integer productId, Model model) {
        model.addAttribute("rows", svc.byProduct(productId));
        model.addAttribute("currentUri", "/inventory/movements");
        return "stock_in_out";
    }

    @PostMapping
    public String addManual(@ModelAttribute StockMovement sm, RedirectAttributes ra) {
        try {
            if (sm.getOccurredAt() == null)
                sm.setOccurredAt(java.time.LocalDateTime.now());
            svc.record(sm);
            ra.addFlashAttribute("success", "Manual stock movement recorded!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error recording movement: " + e.getMessage());
        }
        return "redirect:/inventory/movements";
    }
}
