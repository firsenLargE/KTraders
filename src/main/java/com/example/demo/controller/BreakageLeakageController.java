package com.example.demo.controller;

import com.example.demo.entity.BreakageLeakage;
import com.example.demo.service.BreakageLeakageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventory/breakage")
public class BreakageLeakageController {
    private final BreakageLeakageService svc;

    @Autowired
    public BreakageLeakageController(BreakageLeakageService svc) {
        this.svc = svc;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", svc.all());
        return "breakage_leakage";
    }

    @PostMapping
    public String create(@ModelAttribute BreakageLeakage form) {
        svc.record(form);
        return "redirect:/inventory/breakage";
    }
}


