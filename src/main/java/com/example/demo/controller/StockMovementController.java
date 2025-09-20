package com.example.demo.controller;

import com.example.demo.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller @RequestMapping("/inventory/movements")
public class StockMovementController {
    private final StockMovementService svc;
    @Autowired StockMovementController(StockMovementService svc) { this.svc = svc; }
    @GetMapping("/{productId}") public String view(@PathVariable Integer productId, Model model) {
        model.addAttribute("rows", svc.byProduct(productId)); return "stock_in_out";
    }
}


