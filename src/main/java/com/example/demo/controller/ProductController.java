package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public String showProducts(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        populateSummary(model, products);
        return "products";
    }

    @GetMapping("/products/show-prices")
    public String showPrices(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        model.addAttribute("products", productService.getAllProducts().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName())).toList());
        return "show_prices";
    }

    @GetMapping("/add-product")
    public String showAddProductForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        model.addAttribute("product", new Product());
        return "add-product";
    }

    @org.springframework.beans.factory.annotation.Value("${app.upload.base-dir:uploads}")
    private String uploadBaseDir;

    @org.springframework.beans.factory.annotation.Value("${app.upload.thumbnail.width:400}")
    private int thumbnailWidth;

    @org.springframework.beans.factory.annotation.Value("${app.upload.thumbnail.height:400}")
    private int thumbnailHeight;

    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute Product product, @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        try {
            if (product.getDate() == null)
                product.setDate(LocalDate.now());

            // create per-date subfolder (YYYY/MM)
            LocalDate d = LocalDate.now();
            String year = String.valueOf(d.getYear());
            String month = String.format("%02d", d.getMonthValue());
            Path baseDir = Paths.get(uploadBaseDir);
            Path targetDir = baseDir.resolve(year).resolve(month);
            Files.createDirectories(targetDir);

            if (!imageFile.isEmpty()) {
                // unique filename
                String original = Paths.get(imageFile.getOriginalFilename()).getFileName().toString();
                String fileName = System.currentTimeMillis() + "_" + original.replaceAll("[^A-Za-z0-9._-]", "_");
                Path filePath = targetDir.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // generate thumbnail
                try {
                    Path thumbPath = targetDir.resolve("thumb_" + fileName);
                    net.coobird.thumbnailator.Thumbnails.of(filePath.toFile())
                            .size(thumbnailWidth, thumbnailHeight)
                            .toFile(thumbPath.toFile());
                } catch (Exception ex) {
                    // thumbnail generation failure should not block upload
                    ex.printStackTrace();
                }

                product.setImagePath("/uploads/" + year + "/" + month + "/" + fileName);
            }
            productService.addProduct(product);
            ra.addFlashAttribute("success", "Product added!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/edit-product/{id}")
    public String showEditProductForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        Product product = productService.getProductById(id);
        if (product == null)
            return "redirect:/products";
        model.addAttribute("product", product);
        return "edit-product";
    }

    @PostMapping("/edit-product/{id}")
    public String updateProduct(@PathVariable Integer id, @ModelAttribute Product product, RedirectAttributes ra,
            HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        try {
            product.setId(id);
            productService.updateProduct(product);
            ra.addFlashAttribute("success", "Updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes ra, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        try {
            productService.deleteProduct(id);
            ra.addFlashAttribute("success", "Deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/search-products")
    public String searchProducts(@RequestParam String query, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        List<Product> products = productService.searchProducts(query);
        model.addAttribute("products", products);
        model.addAttribute("searchQuery", query);
        populateSummary(model, products);
        return "products";
    }

    private void populateSummary(Model model, List<Product> products) {
        model.addAttribute("totalProducts", products.size());
        long lowStock = products.stream().filter(p -> (p.getQuantity() != null ? p.getQuantity() : 0) < 10).count();
        model.addAttribute("lowStockProducts", lowStock);
        double totalValue = products.stream()
                .mapToDouble(p -> (p.getPrice() != null ? p.getPrice() : 0.0)
                        * (p.getQuantity() != null ? p.getQuantity() : 0))
                .sum();
        model.addAttribute("totalValue", totalValue);
    }
}
