package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
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

    private static final List<String> SIZES = List.of(
            "1.5L", "1kg", "1L", "2.25L", "2L", "160ml", "175ml", "250g", "250ml",
            "330ml", "500g", "500ml", "650ml", "A3", "A4", "EXTRA LARGE", "LARGE",
            "MEDIUM", "SMALL", "OTHERS").stream().sorted().toList();

    @GetMapping("/products")
    public String showProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        populateSummary(model, products);
        return "products";
    }

    @GetMapping("/products/show-prices")
    public String showPrices(Model model) {
        model.addAttribute("products", productService.getAllProducts().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName())).toList());
        return "show_prices";
    }

    @GetMapping("/products/details/{id}")
    public String showProductDetails(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null)
            return "redirect:/products";
        model.addAttribute("product", product);
        return "product-details";
    }

    @GetMapping("/add-product")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("sizes", SIZES);
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
            RedirectAttributes ra) {
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
    public String showEditProductForm(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null)
            return "redirect:/products";
        model.addAttribute("product", product);
        model.addAttribute("sizes", SIZES);
        return "edit-product";
    }

    @PostMapping("/edit-product/{id}")
    public String updateProduct(@PathVariable Integer id, @ModelAttribute Product product,
            @RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes ra) {
        try {
            product.setId(id);

            // Handle image update if a new file is provided
            if (!imageFile.isEmpty()) {
                // create per-date subfolder (YYYY/MM) - using current date for new upload
                LocalDate d = LocalDate.now();
                String year = String.valueOf(d.getYear());
                String month = String.format("%02d", d.getMonthValue());
                Path baseDir = Paths.get(uploadBaseDir);
                Path targetDir = baseDir.resolve(year).resolve(month);
                Files.createDirectories(targetDir);

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
                    ex.printStackTrace();
                }

                product.setImagePath("/uploads/" + year + "/" + month + "/" + fileName);
            } else {
                // Preserve existing image path if no new file uploaded
                Product existingProduct = productService.getProductById(id);
                if (existingProduct != null) {
                    product.setImagePath(existingProduct.getImagePath());
                }
            }

            productService.updateProduct(product);
            ra.addFlashAttribute("success", "Updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            productService.deleteProduct(id);
            ra.addFlashAttribute("success", "Deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/search-products")
    public String searchProducts(@RequestParam String query, Model model) {
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
