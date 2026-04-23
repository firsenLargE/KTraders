package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.entity.Sale;
import com.example.demo.enums.OrderStatus;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.SaleService;
import com.example.demo.service.UserService;
import com.example.demo.entity.User;
import com.example.demo.service.CustomerService;
import com.example.demo.util.FinancialUtil;
import com.example.demo.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private SaleService saleService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/")
    public String home(java.security.Principal principal) {
        if (principal != null)
            return "redirect:/dashboard";
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage(java.security.Principal principal) {
        if (principal != null)
            return "redirect:/dashboard";
        return "index";
    }

    @GetMapping("/signup")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String postSignup(@ModelAttribute User user, RedirectAttributes ra, Model model) {
        try {
            if (userService.existsByEmail(user.getEmail().toLowerCase())) {
                model.addAttribute("error", "Email already exists.");
                return "signup";
            }
            user.setEmail(user.getEmail().toLowerCase());
            // Encode password before saving
            // userService.signUp should handle this, or we do it here if it's plain
            userService.signUp(user);
            ra.addFlashAttribute("success", "Account created successfully!");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Error creating account: " + e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        List<Product> products = productService.getAllProducts();
        model.addAttribute("totalProducts", productService.countProducts());
        model.addAttribute("lowStockProducts", productService.countLowStockProducts());
        model.addAttribute("totalValue", productService.getTotalInventoryValue());
        model.addAttribute("pendingOrders", orderService.countOrdersByStatus(OrderStatus.PENDING));
        model.addAttribute("processingOrders", orderService.countOrdersByStatus(OrderStatus.PROCESSING));
        model.addAttribute("currentYear", LocalDate.now().getYear());
        model.addAttribute("currentMonth", LocalDate.now().getMonthValue());
        List<Product> recentProducts = products.stream()
                .sorted((p1, p2) -> p2.getDate() != null && p1.getDate() != null ? p2.getDate().compareTo(p1.getDate())
                        : 0)
                .limit(5).toList();
        model.addAttribute("recentProducts", recentProducts);

        List<Sale> recentSales = saleService.getRecentSales(7);
        model.addAttribute("recentSales", recentSales.stream().limit(5).toList());
        return "dashboard";
    }

    @GetMapping("/reports")
    public String showReports(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("productNames",
                products.stream().map(p -> p.getName() != null ? p.getName() : "Unknown").toList());
        model.addAttribute("productQuantities",
                products.stream().map(p -> p.getQuantity() != null ? p.getQuantity() : 0).toList());
        model.addAttribute("categoryCounts",
                products.stream().collect(Collectors.groupingBy(
                        p -> p.getCategory() != null ? p.getCategory() : "Uncategorized", Collectors.counting())));
        return "reports";
    }

    @GetMapping("/api/products")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            if (products == null)
                return ResponseEntity.ok(new ArrayList<>());
            List<Map<String, Object>> productsJson = products.stream().map(p -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", p.getId());
                m.put("name", p.getName() != null ? p.getName() : "Unknown");
                m.put("category", p.getCategory() != null ? p.getCategory() : "Other");
                int price = p.getPrice() != null ? p.getPrice().intValue() : 0;
                int stock = p.getQuantity() != null ? p.getQuantity() : 0;
                int value = FinancialUtil.calculateProductValue(p);
                m.put("price", price);
                m.put("stock", stock);
                m.put("value", value);
                m.put("ratio", price > 0 ? String.format("%.2f", (double) value / price) : "0.00");
                return m;
            }).toList();
            return ResponseEntity.ok(productsJson);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            List<Product> products = productService.getAllProducts();
            Map<String, Object> stats = new HashMap<>();
            if (products == null || products.isEmpty()) {
                stats.put("totalProducts", 0);
                stats.put("avgPrice", 0);
                stats.put("totalValue", 0);
                stats.put("categoriesCount", 0);
            } else {
                stats.put("totalProducts", products.size());
                double avgPrice = products.stream().filter(p -> p.getPrice() != null).mapToDouble(Product::getPrice)
                        .average().orElse(0.0);
                stats.put("avgPrice", Math.round(avgPrice));
                double totalValue = products.stream().mapToDouble(p -> (p.getPrice() != null ? p.getPrice() : 0.0)
                        * (p.getQuantity() != null ? p.getQuantity() : 0)).sum();
                stats.put("totalValue", Math.round(totalValue));
                long categoriesCount = products.stream().map(Product::getCategory).filter(Objects::nonNull).distinct()
                        .count();
                stats.put("categoriesCount", categoriesCount);
            }
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cleanup/capitalize")
    public String capitalize(RedirectAttributes ra) {
        try {
            productService.syncCapitalization();
            customerService.syncCapitalization();
            purchaseService.syncCapitalization();
            ra.addFlashAttribute("success",
                    "Migration complete: All products, customers, and suppliers are now capitalized!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Migration failed: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

}
