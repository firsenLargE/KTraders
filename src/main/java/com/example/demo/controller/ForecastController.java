package com.example.demo.controller;

import com.example.demo.entity.Forecast;
import com.example.demo.entity.Product;
import com.example.demo.service.ForecastService;
import com.example.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller @RequestMapping("/forecasts")
public class ForecastController {
    @Autowired private ForecastService forecastService;
    @Autowired private ProductService productService;

    @GetMapping
    public String showForecasts(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            List<Product> products = productService.getAllProducts();
            Map<Integer, List<Forecast>> productForecasts = new HashMap<>();
            for (Product p : products) {
                try { productForecasts.put(p.getId(), forecastService.getForecastsByProduct(p.getId())); }
                catch (Exception e) { productForecasts.put(p.getId(), new ArrayList<>()); }
            }
            int totalForecasts = productForecasts.values().stream().mapToInt(List::size).sum();
            model.addAttribute("products", products);
            model.addAttribute("productForecasts", productForecasts);
            model.addAttribute("seasons", List.of("SPRING","SUMMER","AUTUMN","WINTER"));
            model.addAttribute("totalProducts", products.size());
            model.addAttribute("totalForecasts", totalForecasts);
        } catch (Exception e) { model.addAttribute("error", "Error loading forecasts"); }
        return "forecasts";
    }

    @PostMapping("/generate/{productId}")
    public String generateForecast(@PathVariable Integer productId, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            Product product = productService.getProductById(productId);
            if (product == null) { ra.addFlashAttribute("error", "Product not found!"); return "redirect:/forecasts"; }
            forecastService.generateForecast(productId);
            ra.addFlashAttribute("success", "Forecast generated!");
        } catch (Exception e) { ra.addFlashAttribute("error", "Error: " + e.getMessage()); }
        return "redirect:/forecasts";
    }

    @PostMapping("/generate-all")
    public String generateAllForecasts(RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            List<Product> products = productService.getAllProducts();
            if (products.isEmpty()) { ra.addFlashAttribute("error", "No products found!"); return "redirect:/forecasts"; }
            int successCount=0, errorCount=0;
            for (Product product : products) {
                try { forecastService.generateForecast(product.getId()); successCount++; }
                catch (Exception e) { errorCount++; }
            }
            if (successCount>0) ra.addFlashAttribute("success","Forecasts generated for "+successCount+" products, errors: "+errorCount);
            else ra.addFlashAttribute("error","No forecasts generated.");
        } catch (Exception e) { ra.addFlashAttribute("error","Error: " + e.getMessage()); }
        return "redirect:/forecasts";
    }
}


