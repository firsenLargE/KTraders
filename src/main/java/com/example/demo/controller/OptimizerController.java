package com.example.demo.controller;

import com.example.demo.dto.PurchasePlanDTO;
import com.example.demo.enums.ReplenishmentStrategy;
import com.example.demo.service.BudgetPlannerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller @RequestMapping("/optimizer")
public class OptimizerController {
    @Autowired private BudgetPlannerService budgetPlannerService;

    @GetMapping public String showOptimizer(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("strategies", ReplenishmentStrategy.values());
        return "optimizer";
    }

    @PostMapping("/plan") @ResponseBody
    public ResponseEntity<PurchasePlanDTO> planPurchases(@RequestParam int budget,
                                                         @RequestParam ReplenishmentStrategy strategy,
                                                         @RequestParam(defaultValue = "false") boolean persist,
                                                         @RequestParam(name="selectedIds", required=false) java.util.Set<Integer> selectedIds,
                                                         HttpSession session) {
        if (session.getAttribute("validuser") == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        PurchasePlanDTO plan = budgetPlannerService.planPurchases(budget, strategy, persist, selectedIds);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/ping") @ResponseBody public String ping() { return "ok"; }
}


