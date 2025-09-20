package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.entity.Sale;
import com.example.demo.enums.OrderStatus;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.SaleService;
import com.example.demo.service.UserService;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    @Autowired private UserService userService;
    @Autowired private ProductService productService;
    @Autowired private SaleService saleService;
    @Autowired private OrderService orderService;

    @GetMapping("/") public String home() { return "index"; }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";

        List<Product> products = productService.getAllProducts();
        model.addAttribute("totalProducts", productService.countProducts());
        model.addAttribute("lowStockProducts", productService.countLowStockProducts());
        model.addAttribute("totalValue", productService.getTotalInventoryValue());
        model.addAttribute("pendingOrders", orderService.countOrdersByStatus(OrderStatus.PENDING));
        model.addAttribute("processingOrders", orderService.countOrdersByStatus(OrderStatus.PROCESSING));
        model.addAttribute("currentYear", LocalDate.now().getYear());
        model.addAttribute("currentMonth", LocalDate.now().getMonthValue());
        List<Product> recentProducts = products.stream()
            .sorted((p1,p2) -> p2.getDate()!=null && p1.getDate()!=null ? p2.getDate().compareTo(p1.getDate()) : 0)
            .limit(5).toList();
        model.addAttribute("recentProducts", recentProducts);

        List<Sale> recentSales = saleService.getRecentSales(7);
        model.addAttribute("recentSales", recentSales.stream().limit(5).toList());
        return "dashboard";
    }

    @GetMapping("/reports")
    public String showReports(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("productNames", products.stream().map(Product::getName).toList());
        model.addAttribute("productQuantities", products.stream().map(p -> p.getQuantity()!=null ? p.getQuantity() : 0).toList());
        model.addAttribute("categoryCounts",
            products.stream().collect(Collectors.groupingBy(Product::getCategory, Collectors.counting())));
        return "reports";
    }

    @GetMapping("/signup") public String registerForm(Model model) { model.addAttribute("user", new User()); return "signup"; }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute User user, Model model, HttpSession session) {
        try {
            User usr = userService.login(user.getEmail().toLowerCase(), user.getPassword());
            if (usr != null) {
                session.setAttribute("validuser", usr);
                session.setMaxInactiveInterval(3600);
                model.addAttribute("uname", usr.getUname());
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Invalid email or password");
                return "index";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return "index";
        }
    }

    @GetMapping("/logout") public String logout(HttpSession session) { session.invalidate(); return "redirect:/"; }

    @PostMapping("/signup")
    public String postSignup(@ModelAttribute User user, RedirectAttributes ra, Model model) {
        try {
            if (userService.existsByEmail(user.getEmail().toLowerCase())) {
                model.addAttribute("error", "Email already exists."); return "signup";
            }
            user.setEmail(user.getEmail().toLowerCase());
            userService.signUp(user);
            ra.addFlashAttribute("success", "Account created successfully!");
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Error creating account: " + e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/api/products") @ResponseBody
    public ResponseEntity<List<Map<String,Object>>> getAllProducts(HttpSession session) {
        if (session.getAttribute("validuser") == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        try {
            List<Product> products = productService.getAllProducts();
            if (products == null) return ResponseEntity.ok(new ArrayList<>());
            List<Map<String,Object>> productsJson = products.stream().map(p -> {
                Map<String,Object> m = new HashMap<>();
                m.put("id", p.getId());
                m.put("name", p.getName() != null ? p.getName() : "Unknown");
                m.put("category", p.getCategory() != null ? p.getCategory() : "Other");
                int price = p.getPrice() != null ? p.getPrice().intValue() : 0;
                int stock = p.getQuantity() != null ? p.getQuantity() : 0;
                int value = calculateProductValue(p, price, stock);
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

    @GetMapping("/api/statistics") @ResponseBody
    public ResponseEntity<Map<String,Object>> getStatistics(HttpSession session) {
        if (session.getAttribute("validuser") == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        try {
            List<Product> products = productService.getAllProducts();
            Map<String,Object> stats = new HashMap<>();
            if (products == null || products.isEmpty()) {
                stats.put("totalProducts", 0); stats.put("avgPrice", 0); stats.put("totalValue", 0); stats.put("categoriesCount", 0);
            } else {
                stats.put("totalProducts", products.size());
                double avgPrice = products.stream().filter(p -> p.getPrice()!=null).mapToDouble(Product::getPrice).average().orElse(0.0);
                stats.put("avgPrice", Math.round(avgPrice));
                double totalValue = products.stream().mapToDouble(p -> (p.getPrice()!=null?p.getPrice():0.0) * (p.getQuantity()!=null?p.getQuantity():0)).sum();
                stats.put("totalValue", Math.round(totalValue));
                long categoriesCount = products.stream().map(Product::getCategory).filter(Objects::nonNull).distinct().count();
                stats.put("categoriesCount", categoriesCount);
            }
            return ResponseEntity.ok(stats);
        } catch (Exception e) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
    }

    private int calculateProductValue(Product p, int price, int stock) {
        double stockFactor = Math.min(Math.max(stock, 0), 100) / 100.0;
        double catFactor;
        String cat = (p.getCategory()!=null) ? p.getCategory().toLowerCase() : "";
        switch (cat) {
            case "electronics": catFactor=0.12; break;
            case "premium":     catFactor=0.10; break;
            case "bestseller":  catFactor=0.08; break;
            case "gadgets":     catFactor=0.05; break;
            default:            catFactor=0.00;
        }
        double v = price * (1.0 + catFactor + 0.20 * stockFactor);
        return Math.max(1, (int)Math.round(v));
    }
}


