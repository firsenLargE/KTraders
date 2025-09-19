package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class ProductController {
    @Autowired private ProductService productService;

    @GetMapping("/products")
    public String showProducts(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        populateSummary(model, products);
        return "products";
    }

    @GetMapping("/products/show-prices")
    public String showPrices(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("products", productService.getAllProducts().stream()
            .sorted((a,b) -> a.getName().compareToIgnoreCase(b.getName())).toList());
        return "show_prices";
    }

    @GetMapping("/products/actual-prices")
    public String actualPrices(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("products", productService.getAllProducts().stream()
            .sorted((a,b) -> a.getName().compareToIgnoreCase(b.getName())).toList());
        return "actual_prices";
    }

    @PostMapping("/products/actual-prices")
    @Transactional
    public String updateActualPrices(@RequestParam Map<String,String> params, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        int updated = 0;
        for (Map.Entry<String,String> e : params.entrySet()) {
            if (!e.getKey().startsWith("actualPrice_")) continue;
            String idStr = e.getKey().substring("actualPrice_".length());
            String v = e.getValue();
            if (v == null || v.isBlank()) continue;
            try {
                Integer id = Integer.valueOf(idStr);
                Product p = productService.getProductById(id);
                if (p != null) {
                    p.setActualPrice(Double.valueOf(v.trim()));
                    productService.updateProduct(p);
                    updated++;
                }
            } catch (NumberFormatException ignore) {}
        }
        ra.addFlashAttribute("success", updated + " product(s) updated.");
        return "redirect:/products/actual-prices";
    }

    @GetMapping("/add-product")
    public String showAddProductForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("product", new Product());
        return "add-product";
    }

    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute Product product, @RequestParam("imageFile") MultipartFile imageFile,
                             RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            if (product.getDate() == null) product.setDate(LocalDate.now());
            String uploadDir = "uploads/"; File uploadPath = new File(uploadDir); if (!uploadPath.exists()) uploadPath.mkdirs();
            if (!imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImagePath("/uploads/" + fileName);
            }
            productService.addProduct(product);
            ra.addFlashAttribute("success", "Product added!");
        } catch (Exception e) { ra.addFlashAttribute("error", "Error: " + e.getMessage()); }
        return "redirect:/products";
    }

    @GetMapping("/edit-product/{id}")
    public String showEditProductForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        Product product = productService.getProductById(id);
        if (product == null) return "redirect:/products";
        model.addAttribute("product", product); return "edit-product";
    }

    @PostMapping("/edit-product/{id}")
    public String updateProduct(@PathVariable Integer id, @ModelAttribute Product product, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            product.setId(id);
            productService.updateProduct(product);
            ra.addFlashAttribute("success", "Updated successfully!");
        } catch (Exception e) { ra.addFlashAttribute("error", "Error: " + e.getMessage()); }
        return "redirect:/products";
    }

    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try { productService.deleteProduct(id); ra.addFlashAttribute("success", "Deleted successfully!"); }
        catch (Exception e) { ra.addFlashAttribute("error", "Error: " + e.getMessage()); }
        return "redirect:/products";
    }

    @GetMapping("/search-products")
    public String searchProducts(@RequestParam String query, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        List<Product> products = productService.searchProducts(query);
        model.addAttribute("products", products);
        model.addAttribute("searchQuery", query);
        populateSummary(model, products);
        return "products";
    }

    private void populateSummary(Model model, List<Product> products) {
        model.addAttribute("totalProducts", products.size());
        long lowStock = products.stream().filter(p -> (p.getQuantity()!=null ? p.getQuantity():0) < 10).count();
        model.addAttribute("lowStockProducts", lowStock);
        double totalValue = products.stream()
            .mapToDouble(p -> (p.getPrice()!=null?p.getPrice():0.0) * (p.getQuantity()!=null?p.getQuantity():0))
            .sum();
        model.addAttribute("totalValue", totalValue);
    }
}


