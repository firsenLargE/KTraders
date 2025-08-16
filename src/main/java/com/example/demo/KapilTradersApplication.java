package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

// import java.util.Optional;
import java.util.stream.Collectors;

// ============ MAIN APPLICATION CLASS ============
@SpringBootApplication
public class KapilTradersApplication {
    public static void main(String[] args) {
        SpringApplication.run(KapilTradersApplication.class, args);
    }
}

// ============ ENTITIES ============




@Entity
class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contact;

    @OneToMany(mappedBy = "customer")
    private List<Sale> sales;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public List<Sale> getSales() { return sales; }
    public void setSales(List<Sale> sales) { this.sales = sales; }
}
@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String uname;
    private String email;
    private String password;
    
    // Constructors
    public User() {}
    public User(String uname, String email, String password) {
        this.uname = uname;
        this.email = email;
        this.password = password;
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUname() { return uname; }
    public void setUname(String uname) { this.uname = uname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

@Entity
@Table(name = "products")
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String category;
    private String size;
    private Integer quantity;
    private Double price;
    private LocalDate date;
    private String imagePath;
    // Constructors
    public Product() {}
    public Product(String name, String category, String size, Integer quantity, Double price, LocalDate date,String imagePath) {
        this.name = name;
        this.category = category;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
		this.imagePath = imagePath;
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
	public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}

@Entity
@Table(name = "sales")
class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer; 

    private Integer quantity;
    private Double totalPrice;
    private LocalDate date;

    public Sale() {}

    public Sale(Product product, Integer quantity, Double totalPrice, LocalDate date) {
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.date = date;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}

// ============ REPOSITORIES ============
@Repository
interface UserRepository extends org.springframework.data.jpa.repository.JpaRepository<User, Long> {
    User findByEmailAndPassword(String email, String password);
    User findByEmail(String email);
    boolean existsByEmail(String email);
}

@Repository
interface ProductRepository extends org.springframework.data.jpa.repository.JpaRepository<Product, Integer> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryContainingIgnoreCase(String category);
    long countByQuantityLessThan(int quantity);
    List<Product> findByQuantityLessThan(int quantity);
    
    @Query("SELECT p FROM Product p ORDER BY p.date DESC")
    List<Product> findAllOrderByDateDesc();
	List<Product> findByImagePathIsNotNull();
    List<Product> findByImagePathIsNull();
}
@Repository
interface SaleRepository extends org.springframework.data.jpa.repository.JpaRepository<Sale, Integer> {
    List<Sale> findByDate(LocalDate date);
}

@Repository
interface CustomerRepository extends org.springframework.data.jpa.repository.JpaRepository<Customer, Long> {
	// List<Customer> findByNameContainingIgnoreCase(String name);	
}
// ============ SERVICES ============
interface UserService {
    void signUp(User user);
    User login(String email, String password);
    User findByEmail(String email);
    boolean existsByEmail(String email);
}

interface ProductService {
    void addProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(Integer id);
    void updateProduct(Product product);
    List<Product> searchProducts(String name);
    void deleteProduct(Integer id);
    long countProducts();
    long countLowStockProducts();
    double getTotalInventoryValue();
	

}
interface SaleService {
    void addSale(Sale sale);
    List<Sale> getAllSales();
    Sale getSaleById(Integer id); // <-- your method
}
interface CustomerService {
    void saveCustomer(Customer customer);
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    void deleteCustomer(Long id);
}

@Service
class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void signUp(User user) {
        userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
    
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

@Service
class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void addProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public void updateProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepository.findById(id).ifPresent(productRepository::delete);
    }

    @Override
    public long countProducts() {
        return productRepository.count();
    }

    @Override
    public long countLowStockProducts() {
        return productRepository.countByQuantityLessThan(10);
    }

    @Override
    public double getTotalInventoryValue() {
        List<Product> products = productRepository.findAll();
        return products.stream()
            .mapToDouble(p -> p.getPrice() * p.getQuantity())
            .sum();
    }
}
@Service
class SaleServiceImpl implements SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Override
    public void addSale(Sale sale) {
        saleRepository.save(sale);
    }

    @Override
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    @Override
    public Sale getSaleById(Integer id) {
        return saleRepository.findById(id).orElse(null);
    }
}


@Service
 class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
    }


	@Override
	public void deleteCustomer(Long id) {
		        customerRepository.deleteById(id);

	}
}

// ============ CONTROLLERS ============
@Controller
class HomeController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;

	@Autowired
	private SaleService saleService;


    @GetMapping("/")
    public String home() {
        return "index";
    }

   @GetMapping("/dashboard")
public String showDashboard(Model model, HttpSession session) {
    if (session.getAttribute("validuser") == null) {
        return "redirect:/";
    }
    
    // Dashboard statistics
    List<Product> products = productService.getAllProducts();
    long totalProducts = productService.countProducts();
    long lowStockProducts = productService.countLowStockProducts();
    double totalValue = productService.getTotalInventoryValue();
    
    // Recent products
    List<Product> recentProducts = products.stream()
            .sorted((p1, p2) -> p2.getDate() != null && p1.getDate() != null ? 
                p2.getDate().compareTo(p1.getDate()) : 0)
            .limit(5)
            .toList();
    
    // Optional: recent sales summary
    List<Sale> recentSales = saleService.getAllSales().stream().limit(5).toList();
    
    model.addAttribute("totalProducts", totalProducts);
    model.addAttribute("lowStockProducts", lowStockProducts);
    model.addAttribute("totalValue", totalValue);
    model.addAttribute("recentProducts", recentProducts);
    model.addAttribute("recentSales", recentSales);  // optional
    
    return "dashboard";
}

@GetMapping("/reports")
public String showReports(Model model, HttpSession session) {
    if (session.getAttribute("validuser") == null) return "redirect:/";

    List<Product> products = productService.getAllProducts();
    model.addAttribute("products", products);

    // Prepare arrays for chart.js
    List<String> productNames = products.stream()
                                        .map(Product::getName)
                                        .toList();
    List<Integer> productQuantities = products.stream()
                                              .map(Product::getQuantity)
                                              .map(q -> q != null ? q : 0)
                                              .toList();
    // Category distribution
    Map<String, Long> categoryCounts = products.stream()
            .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

    model.addAttribute("productNames", productNames);
    model.addAttribute("productQuantities", productQuantities);
    model.addAttribute("categoryCounts", categoryCounts);

    return "reports";
}



    @GetMapping("/signup")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute User user, Model model, HttpSession session) {
        try {
            User usr = userService.login(user.getEmail().toLowerCase(), user.getPassword());
            
            if (usr != null) {
                session.setAttribute("validuser", usr);
                session.setMaxInactiveInterval(3600); // 1 hour session
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
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/signup")
    public String postSignup(@ModelAttribute User user, 
                           RedirectAttributes redirectAttributes, 
                           Model model) {
        try {
            // Check if user already exists
            if (userService.existsByEmail(user.getEmail().toLowerCase())) {
                model.addAttribute("error", "Email already exists. Please use a different email.");
                return "signup";
            }
            
            // Normalize email
            user.setEmail(user.getEmail().toLowerCase());
            
            userService.signUp(user);
            redirectAttributes.addFlashAttribute("success", "Account created successfully! Please login.");
            
            return "redirect:/";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error creating account: " + e.getMessage());
            return "signup";
        }
    }
}

@Controller
class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public String showProducts(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }

        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        populateSummary(model, products);
        return "products";
    }

    @GetMapping("/add-product")
    public String showAddProductForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }

        model.addAttribute("product", new Product());
        return "add-product";
    }

@PostMapping("/add-product")
public String addProduct(@ModelAttribute Product product,
                         @RequestParam("imageFile") MultipartFile imageFile,
                         RedirectAttributes redirectAttributes,
                         HttpSession session) {
    if (session.getAttribute("validuser") == null) {
        return "redirect:/";
    }

    try {
        if (product.getDate() == null) {
            product.setDate(LocalDate.now());
        }

        // External upload directory (relative to where the app runs)
        String uploadDir = "uploads/";
        File uploadPath = new File(uploadDir);

        if (!uploadPath.exists()) {
            uploadPath.mkdirs(); // create if not exists
        }

        if (!imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Store relative URL path (for browser access)
            product.setImagePath("/uploads/" + fileName);
        }

        productService.addProduct(product);
        redirectAttributes.addFlashAttribute("success", "Product added successfully!");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error adding product: " + e.getMessage());
    }

    return "redirect:/products";
}


    @GetMapping("/edit-product/{id}")
    public String showEditProductForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }

        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/products";
        }

        model.addAttribute("product", product);
        return "edit-product";
    }

    @PostMapping("/edit-product/{id}")
    public String updateProduct(@PathVariable Integer id,
                                @ModelAttribute Product product,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }

        try {
            product.setId(id);
            productService.updateProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating product: " + e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Integer id,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }

        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/search-products")
    public String searchProducts(@RequestParam String query, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }

        List<Product> products = productService.searchProducts(query);
        model.addAttribute("products", products);
        model.addAttribute("searchQuery", query);
        populateSummary(model, products);
        return "products";
    }

    private void populateSummary(Model model, List<Product> products) {
        model.addAttribute("totalProducts", products.size());
        long lowStock = products.stream()
                .filter(p -> safeQuantity(p) < 10)
                .count();
        model.addAttribute("lowStockProducts", lowStock);

        double totalValue = products.stream()
                .mapToDouble(p -> safePrice(p) * safeQuantity(p))
                .sum();
        model.addAttribute("totalValue", totalValue);
    }

    private int safeQuantity(Product p) {
        Integer q = p.getQuantity();
        return q != null ? q : 0;
    }

    private double safePrice(Product p) {
        Double price = p.getPrice();
        return price != null ? price : 0.0;
    }
}

@Controller
class SaleController {

    @Autowired
    private SaleService saleService;

    @Autowired
    private ProductService productService;

	@Autowired
	private CustomerService customerService;

	// Ensure this service is defined
	// @Autowired
	// private UserService userService;

 @GetMapping("/sales")
public String showSalesForm(Model model, HttpSession session) {
    if (session.getAttribute("validuser") == null) {
        return "redirect:/";
    }
    
    model.addAttribute("sale", new Sale());        // Must exist
    model.addAttribute("products", productService.getAllProducts());
	model.addAttribute("customers", customerService.getAllCustomers());
    model.addAttribute("sales", saleService.getAllSales());
	model.addAttribute("customer", new Customer());

    
    return "sales";
}

    @GetMapping("/sales/add")
    public String showAddSaleForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";

        model.addAttribute("sale", new Sale());
        model.addAttribute("products", productService.getAllProducts());
        return "add-sale";
    }

  @PostMapping("/sales/add")
public String addSale(@ModelAttribute Sale sale,
                      RedirectAttributes redirectAttributes,
                      HttpSession session) {

    if (session.getAttribute("validuser") == null) return "redirect:/";

    try {
        // Fetch full Product and Customer objects using IDs from the form
        Product product = productService.getProductById(sale.getProduct().getId());
        Customer customer = customerService.getCustomerById(sale.getCustomer().getId());

        if (product == null || sale.getQuantity() > product.getQuantity()) {
            redirectAttributes.addFlashAttribute("error", "Invalid product or insufficient stock");
            return "redirect:/sales";
        }

        // Set the fetched objects to the sale
        sale.setProduct(product);
        sale.setCustomer(customer);

        // Reduce product stock
        product.setQuantity(product.getQuantity() - sale.getQuantity());
        productService.updateProduct(product);

        // Calculate total price
        sale.setTotalPrice(product.getPrice() * sale.getQuantity());
        sale.setDate(LocalDate.now());

        saleService.addSale(sale);
        redirectAttributes.addFlashAttribute("success", "Sale added successfully!");

    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error adding sale: " + e.getMessage());
    }

    return "redirect:/sales";
}


    @GetMapping("/sales/{id}/receipt")
    public void generateReceipt(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Sale sale = saleService.getSaleById(id);

        if (sale == null) return;

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=receipt_" + id + ".pdf");

        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        try {
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            document.add(new com.itextpdf.text.Paragraph("----- Invoice Kapil-----"));
            document.add(new com.itextpdf.text.Paragraph("Sale ID: " + sale.getId()));
            document.add(new com.itextpdf.text.Paragraph("Product: " + sale.getProduct().getName()));
            document.add(new com.itextpdf.text.Paragraph("Quantity: " + sale.getQuantity()));
            document.add(new com.itextpdf.text.Paragraph("Price per unit: " + sale.getProduct().getPrice()));
            document.add(new com.itextpdf.text.Paragraph("Total Price: " + sale.getTotalPrice()));
            document.add(new com.itextpdf.text.Paragraph("Date: " + sale.getDate()));
            document.add(new com.itextpdf.text.Paragraph("--------------------------"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}

@Controller
@RequestMapping("/customers")
 class CustomerController {

    @Autowired
    private CustomerService customerService;

    // List all customers
    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("customer", new Customer()); // For the Add form
        return "customers"; // Matches your customers.html
    }
    @GetMapping("/add") // URL: /customers/add
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customers"; // Thymeleaf template
    }

    @PostMapping("/add") // URL: /customers/add
    public String saveOrUpdateCustomer(@ModelAttribute Customer customer) {
        customerService.saveCustomer(customer);
        return "redirect:/customers"; // redirect to the customer list
    }

    // Show edit form (populates the same customers.html)
    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable Long id, Model model) {
        Customer customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer); // Populates the form
        model.addAttribute("customers", customerService.getAllCustomers()); // Table data
        return "customers"; // Reuse the same template
    }

    // Delete customer
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }
}



// ============ DATA INITIALIZER ============
@Component
class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default admin user if no users exist
        if (userRepository.count() == 0) {
            User adminUser = new User();
            adminUser.setUname("admin");
            adminUser.setEmail("admin@kapiltraders.com");
            adminUser.setPassword("admin123"); // In a real app, hash this password
            userRepository.save(adminUser);

            System.out.println("Created default admin user:");
            System.out.println("Email: admin@kapiltraders.com");
            System.out.println("Password: admin123");
        }
        
    }
}
// Add these new entities to your existing KapilTradersApplication.java file


























































// ============ NEW ENTITIES FOR ADVANCED FEATURES ============

@Entity
@Table(name = "demand_forecasts")
class DemandForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    private LocalDate forecastDate;
    private Integer predictedDemand;
    private Double confidence;
    private String season; // SPRING, SUMMER, AUTUMN, WINTER
    private LocalDate createdAt;
    // In DemandForecast class
private Double confidencePercentage;
private String confidenceDisplay;

public Double getConfidencePercentage() {
    return confidencePercentage;
}


public void setConfidencePercentage(Double confidencePercentage) {
    this.confidencePercentage = confidencePercentage;
}
public String getConfidenceDisplay() {
    return confidenceDisplay;
}

public void setConfidenceDisplay(String confidenceDisplay) {
    this.confidenceDisplay = confidenceDisplay;
}
    public DemandForecast() {}
    
    public DemandForecast(Product product, LocalDate forecastDate, Integer predictedDemand, 
                         Double confidence, String season) {
        this.product = product;
        this.forecastDate = forecastDate;
        this.predictedDemand = predictedDemand;
        this.confidence = confidence;
        this.season = season;
        this.createdAt = LocalDate.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public LocalDate getForecastDate() { return forecastDate; }
    public void setForecastDate(LocalDate forecastDate) { this.forecastDate = forecastDate; }
    public Integer getPredictedDemand() { return predictedDemand; }
    public void setPredictedDemand(Integer predictedDemand) { this.predictedDemand = predictedDemand; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}

@Entity
@Table(name = "purchase_orders")
class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    private Integer recommendedQuantity;
    private Integer economicOrderQuantity;
    private LocalDate suggestedOrderDate;
    private LocalDate expectedDeliveryDate;
    private String status; // PENDING, ORDERED, DELIVERED
    private String supplier;
    private Double estimatedCost;
    private String reason; // LOW_STOCK, FORECAST_DEMAND, etc.
    private LocalDate createdAt;
    
    public PurchaseOrder() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getRecommendedQuantity() { return recommendedQuantity; }
    public void setRecommendedQuantity(Integer recommendedQuantity) { this.recommendedQuantity = recommendedQuantity; }
    public Integer getEconomicOrderQuantity() { return economicOrderQuantity; }
    public void setEconomicOrderQuantity(Integer economicOrderQuantity) { this.economicOrderQuantity = economicOrderQuantity; }
    public LocalDate getSuggestedOrderDate() { return suggestedOrderDate; }
    public void setSuggestedOrderDate(LocalDate suggestedOrderDate) { this.suggestedOrderDate = suggestedOrderDate; }
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}

@Entity
@Table(name = "business_metrics")
class BusinessMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate date;
    private Double totalRevenue;
    private Double totalProfit;
    private Integer totalSales;
    private Integer uniqueCustomers;
    private Double averageOrderValue;
    private String topSellingCategory;
    private String topSellingProduct;
    private Double inventoryTurnover;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    public Double getTotalProfit() { return totalProfit; }
    public void setTotalProfit(Double totalProfit) { this.totalProfit = totalProfit; }
    public Integer getTotalSales() { return totalSales; }
    public void setTotalSales(Integer totalSales) { this.totalSales = totalSales; }
    public Integer getUniqueCustomers() { return uniqueCustomers; }
    public void setUniqueCustomers(Integer uniqueCustomers) { this.uniqueCustomers = uniqueCustomers; }
    public Double getAverageOrderValue() { return averageOrderValue; }
    public void setAverageOrderValue(Double averageOrderValue) { this.averageOrderValue = averageOrderValue; }
    public String getTopSellingCategory() { return topSellingCategory; }
    public void setTopSellingCategory(String topSellingCategory) { this.topSellingCategory = topSellingCategory; }
    public String getTopSellingProduct() { return topSellingProduct; }
    public void setTopSellingProduct(String topSellingProduct) { this.topSellingProduct = topSellingProduct; }
    public Double getInventoryTurnover() { return inventoryTurnover; }
    public void setInventoryTurnover(Double inventoryTurnover) { this.inventoryTurnover = inventoryTurnover; }
}

// ============ NEW REPOSITORIES ============

@Repository
interface DemandForecastRepository extends JpaRepository<DemandForecast, Long> {
    List<DemandForecast> findByProductId(Integer productId);
    List<DemandForecast> findByForecastDateBetween(LocalDate startDate, LocalDate endDate);
    @Query("SELECT df FROM DemandForecast df WHERE df.forecastDate >= :startDate ORDER BY df.confidence DESC")
    List<DemandForecast> findUpcomingForecastsOrderByConfidence(@Param("startDate") LocalDate startDate);
}

@Repository
interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByStatus(String status);
    List<PurchaseOrder> findByProductId(Integer productId);
    @Query("SELECT po FROM PurchaseOrder po WHERE po.suggestedOrderDate <= :date AND po.status = 'PENDING'")
    List<PurchaseOrder> findDuePurchaseOrders(@Param("date") LocalDate date);
}

@Repository
interface BusinessMetricsRepository extends JpaRepository<BusinessMetrics, Long> {
    BusinessMetrics findByDate(LocalDate date);
    List<BusinessMetrics> findByDateBetweenOrderByDateDesc(LocalDate startDate, LocalDate endDate);
    @Query("SELECT bm FROM BusinessMetrics bm ORDER BY bm.date DESC")
    List<BusinessMetrics> findAllOrderByDateDesc();
}

// ============ NEW SERVICES ============

interface DemandForecastService {
    void generateForecasts();
    List<DemandForecast> getForecastsForProduct(Integer productId);
    List<DemandForecast> getUpcomingForecasts();
    DemandForecast generateSingleProductForecast(Product product);
}

interface PurchaseOrderService {
    List<PurchaseOrder> generateAutomaticOrders();
    PurchaseOrder createPurchaseOrder(Product product, String reason);
    List<PurchaseOrder> getPendingOrders();
    void updateOrderStatus(Long orderId, String status);
    List<PurchaseOrder> getDueOrders();
}

interface BusinessIntelligenceService {
    BusinessMetrics generateTodayMetrics();
    BusinessMetrics getMetricsForDate(LocalDate date);
    List<BusinessMetrics> getMetricsForPeriod(LocalDate startDate, LocalDate endDate);
    Map<String, Object> getDashboardData();
    Map<String, Object> getAdvancedAnalytics();
}

@Service
class DemandForecastServiceImpl implements DemandForecastService {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private SaleService saleService;
    
    @Autowired
    private DemandForecastRepository demandForecastRepository;
    
    @Override
    public void generateForecasts() {
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            generateSingleProductForecast(product);
        }
    }
    
    @Override
    public DemandForecast generateSingleProductForecast(Product product) {
        // Simple moving average forecasting algorithm
        List<Sale> sales = saleService.getAllSales().stream()
            .filter(sale -> sale.getProduct().getId().equals(product.getId()))
            .filter(sale -> sale.getDate().isAfter(LocalDate.now().minusDays(90)))
            .collect(Collectors.toList());
        
        if (sales.isEmpty()) {
            // No sales data, predict based on current stock level
            DemandForecast forecast = new DemandForecast(
                product, 
                LocalDate.now().plusDays(30),
                Math.max(1, product.getQuantity() / 10),
                0.3,
                getCurrentSeason()
            );
            return demandForecastRepository.save(forecast);
        }
        
        // Calculate average daily sales
        double totalSold = sales.stream().mapToInt(Sale::getQuantity).sum();
        long dayRange = Math.max(1, sales.stream()
            .map(Sale::getDate)
            .distinct()
            .count());
        
        double averageDailySales = totalSold / dayRange;
        
        // Apply seasonal adjustment
        double seasonalMultiplier = getSeasonalMultiplier(getCurrentSeason(), product.getCategory());
        
        // Predict demand for next 30 days
        int predictedDemand = (int) Math.ceil(averageDailySales * 30 * seasonalMultiplier);
        
        // Calculate confidence based on data consistency
        double confidence = Math.min(0.95, Math.max(0.1, dayRange / 90.0));
        
        DemandForecast forecast = new DemandForecast(
            product,
            LocalDate.now().plusDays(30),
            predictedDemand,
            confidence,
            getCurrentSeason()
        );
        
        return demandForecastRepository.save(forecast);
    }
    
    @Override
    public List<DemandForecast> getForecastsForProduct(Integer productId) {
        return demandForecastRepository.findByProductId(productId);
    }
    
    @Override
    public List<DemandForecast> getUpcomingForecasts() {
        return demandForecastRepository.findUpcomingForecastsOrderByConfidence(LocalDate.now());
    }
    
    private String getCurrentSeason() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 3 && month <= 5) return "SPRING";
        if (month >= 6 && month <= 8) return "SUMMER";
        if (month >= 9 && month <= 11) return "AUTUMN";
        return "WINTER";
    }
    
    private double getSeasonalMultiplier(String season, String category) {
        // Simple seasonal adjustments - you can make this more sophisticated
        Map<String, Double> seasonalFactors = Map.of(
            "SUMMER", 1.2,
            "WINTER", 0.9,
            "SPRING", 1.1,
            "AUTUMN", 1.0
        );
        return seasonalFactors.getOrDefault(season, 1.0);
    }
}

@Service
class PurchaseOrderServiceImpl implements PurchaseOrderService {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private DemandForecastService demandForecastService;
    
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    
    @Override
    public List<PurchaseOrder> generateAutomaticOrders() {
        List<PurchaseOrder> newOrders = new ArrayList<>();
        List<Product> products = productService.getAllProducts();
        
        for (Product product : products) {
            // Check if product needs reordering
            if (shouldReorder(product)) {
                PurchaseOrder order = createPurchaseOrder(product, determineReorderReason(product));
                newOrders.add(order);
            }
        }
        
        return newOrders;
    }
    
    @Override
    public PurchaseOrder createPurchaseOrder(Product product, String reason) {
        PurchaseOrder order = new PurchaseOrder();
        order.setProduct(product);
        order.setReason(reason);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDate.now());
        order.setSuggestedOrderDate(LocalDate.now());
        order.setExpectedDeliveryDate(LocalDate.now().plusDays(7)); // 1 week delivery
        
        // Calculate EOQ (Economic Order Quantity) - simplified version
        int eoq = calculateEOQ(product);
        order.setEconomicOrderQuantity(eoq);
        
        // Get forecast-based recommendation
        List<DemandForecast> forecasts = demandForecastService.getForecastsForProduct(product.getId());
        int forecastDemand = forecasts.stream()
            .mapToInt(DemandForecast::getPredictedDemand)
            .sum();
        
        int recommendedQty = Math.max(eoq, forecastDemand - product.getQuantity());
        order.setRecommendedQuantity(Math.max(1, recommendedQty));
        
        order.setEstimatedCost(product.getPrice() * order.getRecommendedQuantity() * 0.7); // 30% profit margin
        order.setSupplier("Auto-Generated Supplier");
        
        return purchaseOrderRepository.save(order);
    }
    
    @Override
    public List<PurchaseOrder> getPendingOrders() {
        return purchaseOrderRepository.findByStatus("PENDING");
    }
    
    @Override
    public void updateOrderStatus(Long orderId, String status) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            purchaseOrderRepository.save(order);
        }
    }
    
    @Override
    public List<PurchaseOrder> getDueOrders() {
        return purchaseOrderRepository.findDuePurchaseOrders(LocalDate.now());
    }
    
    private boolean shouldReorder(Product product) {
        int currentStock = product.getQuantity() != null ? product.getQuantity() : 0;
        
        // Reorder if stock is below 10 units
        if (currentStock < 10) return true;
        
        // Check forecasts
        List<DemandForecast> forecasts = demandForecastService.getForecastsForProduct(product.getId());
        int expectedDemand = forecasts.stream()
            .filter(f -> f.getForecastDate().isBefore(LocalDate.now().plusDays(30)))
            .mapToInt(DemandForecast::getPredictedDemand)
            .sum();
        
        return currentStock < expectedDemand;
    }
    
    private String determineReorderReason(Product product) {
        if (product.getQuantity() < 10) return "LOW_STOCK";
        return "FORECAST_DEMAND";
    }
    
    private int calculateEOQ(Product product) {
        // Simplified EOQ calculation
        // In reality, you'd need ordering cost and holding cost
        double annualDemand = 365.0; // Assume 1 unit per day
        double orderingCost = 50.0; // Fixed cost per order
        double holdingCost = product.getPrice() * 0.1; // 10% of item cost per year
        
        return (int) Math.ceil(Math.sqrt((2 * annualDemand * orderingCost) / holdingCost));
    }
}

@Service
class BusinessIntelligenceServiceImpl implements BusinessIntelligenceService {
    
    @Autowired
    private SaleService saleService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private BusinessMetricsRepository businessMetricsRepository;
    
    @Override
    public BusinessMetrics generateTodayMetrics() {
        LocalDate today = LocalDate.now();
        
        // Check if metrics already exist for today
        BusinessMetrics existing = businessMetricsRepository.findByDate(today);
        if (existing != null) {
            return existing;
        }
        
        BusinessMetrics metrics = new BusinessMetrics();
        metrics.setDate(today);
        
        List<Sale> todaySales = saleService.getAllSales().stream()
            .filter(sale -> sale.getDate().equals(today))
            .collect(Collectors.toList());
        
        // Calculate metrics
        metrics.setTotalRevenue(todaySales.stream().mapToDouble(Sale::getTotalPrice).sum());
        metrics.setTotalSales(todaySales.size());
        
        // Calculate profit (assuming 30% profit margin)
        metrics.setTotalProfit(metrics.getTotalRevenue() * 0.3);
        
        // Unique customers
        metrics.setUniqueCustomers((int) todaySales.stream()
            .filter(sale -> sale.getCustomer() != null)
            .map(sale -> sale.getCustomer().getId())
            .distinct()
            .count());
        
        // Average order value
        metrics.setAverageOrderValue(
            todaySales.isEmpty() ? 0.0 : metrics.getTotalRevenue() / todaySales.size()
        );
        
        // Top selling product and category
        Map<String, Long> productSales = todaySales.stream()
            .collect(Collectors.groupingBy(
                sale -> sale.getProduct().getName(),
                Collectors.counting()
            ));
        
        metrics.setTopSellingProduct(
            productSales.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None")
        );
        
        Map<String, Long> categorySales = todaySales.stream()
            .collect(Collectors.groupingBy(
                sale -> sale.getProduct().getCategory(),
                Collectors.counting()
            ));
        
        metrics.setTopSellingCategory(
            categorySales.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None")
        );
        
        // Inventory turnover (simplified)
        double totalInventoryValue = productService.getTotalInventoryValue();
        metrics.setInventoryTurnover(
            totalInventoryValue > 0 ? metrics.getTotalRevenue() / totalInventoryValue : 0.0
        );
        
        return businessMetricsRepository.save(metrics);
    }
    
    @Override
    public BusinessMetrics getMetricsForDate(LocalDate date) {
        return businessMetricsRepository.findByDate(date);
    }
    
    @Override
    public List<BusinessMetrics> getMetricsForPeriod(LocalDate startDate, LocalDate endDate) {
        return businessMetricsRepository.findByDateBetweenOrderByDateDesc(startDate, endDate);
    }
    
    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        LocalDate monthAgo = today.minusDays(30);
        
        // Generate today's metrics
        BusinessMetrics todayMetrics = generateTodayMetrics();
        
        // Get recent metrics
        List<BusinessMetrics> weeklyMetrics = getMetricsForPeriod(weekAgo, today);
        List<BusinessMetrics> monthlyMetrics = getMetricsForPeriod(monthAgo, today);
        
        data.put("todayMetrics", todayMetrics);
        data.put("weeklyRevenue", weeklyMetrics.stream().mapToDouble(BusinessMetrics::getTotalRevenue).sum());
        data.put("monthlyRevenue", monthlyMetrics.stream().mapToDouble(BusinessMetrics::getTotalRevenue).sum());
        data.put("weeklyProfit", weeklyMetrics.stream().mapToDouble(BusinessMetrics::getTotalProfit).sum());
        data.put("monthlyProfit", monthlyMetrics.stream().mapToDouble(BusinessMetrics::getTotalProfit).sum());
        
        // Growth rates
        if (weeklyMetrics.size() >= 2) {
            double currentWeekRevenue = weeklyMetrics.stream()
                .filter(m -> m.getDate().isAfter(today.minusDays(7)))
                .mapToDouble(BusinessMetrics::getTotalRevenue)
                .sum();
            double previousWeekRevenue = weeklyMetrics.stream()
                .filter(m -> m.getDate().isBefore(today.minusDays(7)))
                .mapToDouble(BusinessMetrics::getTotalRevenue)
                .sum();
            
            double growthRate = previousWeekRevenue > 0 ? 
                ((currentWeekRevenue - previousWeekRevenue) / previousWeekRevenue) * 100 : 0;
            data.put("revenueGrowthRate", growthRate);
        }
        
        return data;
    }
    
    @Override
    public Map<String, Object> getAdvancedAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        List<Sale> allSales = saleService.getAllSales();
        List<Product> allProducts = productService.getAllProducts();
        
        // ABC Analysis
        Map<String, Double> productRevenue = allSales.stream()
            .collect(Collectors.groupingBy(
                sale -> sale.getProduct().getName(),
                Collectors.summingDouble(Sale::getTotalPrice)
            ));
        
        analytics.put("productRevenue", productRevenue);
        
        // Sales trend over time
        Map<LocalDate, Double> dailySales = allSales.stream()
            .collect(Collectors.groupingBy(
                Sale::getDate,
                Collectors.summingDouble(Sale::getTotalPrice)
            ));
        
        analytics.put("dailySales", dailySales);
        
        // Category performance
        Map<String, Double> categoryRevenue = allSales.stream()
            .collect(Collectors.groupingBy(
                sale -> sale.getProduct().getCategory(),
                Collectors.summingDouble(Sale::getTotalPrice)
            ));
        
        analytics.put("categoryRevenue", categoryRevenue);
        
        return analytics;
    }
}
// Add these new controllers to your existing KapilTradersApplication.java file

// ============ NEW CONTROLLERS ============

@Controller
@RequestMapping("/forecasting")
 class ForecastingController {

    @Autowired
    private DemandForecastService demandForecastService;

    @Autowired
    private ProductService productService;

    // Display forecasting dashboard
    @GetMapping
    public String showForecasting(Model model) {
        List<DemandForecast> forecasts = demandForecastService.getUpcomingForecasts();

        // Precompute confidence display string safely
        for (DemandForecast f : forecasts) {
            if (f.getConfidence() != null) {
                f.setConfidenceDisplay(String.format("%.1f%%", f.getConfidence() * 100));
            } else {
                f.setConfidenceDisplay("N/A");
            }
        }

        int totalPredictedDemand = forecasts.stream()
                .filter(f -> f.getPredictedDemand() != null)
                .mapToInt(DemandForecast::getPredictedDemand)
                .sum();

        long highConfidenceCount = forecasts.stream()
                .filter(f -> f.getConfidence() != null && f.getConfidence() >= 0.8)
                .count();

        model.addAttribute("forecasts", forecasts);
        model.addAttribute("totalPredictedDemand", totalPredictedDemand);
        model.addAttribute("highConfidenceCount", highConfidenceCount);

        return "forecasting";
    }

    // Generate forecasts for all products
    @PostMapping("/generate")
    public String generateForecasts(RedirectAttributes redirectAttrs) {
        try {
            demandForecastService.generateForecasts();
            redirectAttrs.addFlashAttribute("success", "Forecasts generated successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error generating forecasts: " + e.getMessage());
        }
        return "redirect:/forecasting";
    }

    // Generate forecast for a single product
    @PostMapping("/generate/{productId}")
    public String generateSingleProductForecast(@PathVariable Integer productId,
                                                RedirectAttributes redirectAttrs) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            redirectAttrs.addFlashAttribute("error", "Product not found!");
            return "redirect:/forecasting";
        }

        try {
            demandForecastService.generateSingleProductForecast(product);
            redirectAttrs.addFlashAttribute("success", "Forecast generated for " + product.getName() + "!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error generating forecast: " + e.getMessage());
        }

        return "redirect:/forecasting";
    }


    // @PostMapping("/generate/{productId}")
    // public String generateSingleProductForecast(@PathVariable Integer productId, RedirectAttributes redirectAttrs) {
    //     Product product = productService.getProductById(productId);
    //     if (product == null) {
    //         redirectAttrs.addFlashAttribute("error", "Product not found!");
    //         return "redirect:/forecasting";
    //     }

    //     try {
    //         demandForecastService.generateSingleProductForecast(product);
    //         redirectAttrs.addFlashAttribute("success", "Forecast generated for " + product.getName() + "!");
    //     } catch (Exception e) {
    //         redirectAttrs.addFlashAttribute("error", "Error generating forecast: " + e.getMessage());
    //     }

    //     return "redirect:/forecasting";
    // }



    // // Optional: Single product forecast generation (example usage)
    // @PostMapping("/generate/{productId}")
    // public String generateSingleProductForecast(@PathVariable Integer productId, RedirectAttributes redirectAttributes) {
    //     Product product = productService.getProductById(productId);
    //     if (product == null) {
    //         redirectAttributes.addFlashAttribute("error", "Product not found!");
    //         return "redirect:/forecasting";
    //     }

    //     try {
    //         demandForecastService.generateSingleProductForecast(product);
    //         redirectAttributes.addFlashAttribute("success", "Forecast generated for " + product.getName() + "!");
    //     } catch (Exception e) {
    //         redirectAttributes.addFlashAttribute("error", "Error generating forecast: " + e.getMessage());
    //     }

    //     return "redirect:/forecasting";
    // }

    // Optional utility method for Thymeleaf: check if reorder is needed
    public boolean isReorderNeeded(Product product) {
        List<DemandForecast> forecasts = demandForecastService.getForecastsForProduct(product.getId());
        int expectedDemand = forecasts.stream()
            .filter(f -> f.getForecastDate().isBefore(LocalDate.now().plusDays(30)))
            .mapToInt(DemandForecast::getPredictedDemand)
            .sum();

        return product.getQuantity() < expectedDemand; // corrected
    }


    
    @GetMapping("/product/{id}")
    @ResponseBody
    public List<DemandForecast> getProductForecasts(@PathVariable Integer id) {
        return demandForecastService.getForecastsForProduct(id);
    }
}

@Controller
@RequestMapping("/purchase-orders")
class PurchaseOrderController {
    
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public String showPurchaseOrders(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }
        
        List<PurchaseOrder> pendingOrders = purchaseOrderService.getPendingOrders();
        List<PurchaseOrder> dueOrders = purchaseOrderService.getDueOrders();
        
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("dueOrders", dueOrders);
        
        return "purchase-orders";
    }
    
    @PostMapping("/generate-automatic")
    public String generateAutomaticOrders(RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }
        
        try {
            List<PurchaseOrder> newOrders = purchaseOrderService.generateAutomaticOrders();
            redirectAttributes.addFlashAttribute("success", 
                "Generated " + newOrders.size() + " automatic purchase orders!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error generating orders: " + e.getMessage());
        }
        
        return "redirect:/purchase-orders";
    }
    
    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id, 
                                   @RequestParam String status,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }
        
        try {
            purchaseOrderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Order status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating order status: " + e.getMessage());
        }
        
        return "redirect:/purchase-orders";
    }
}

@Controller
@RequestMapping("/business-intelligence")
class BusinessIntelligenceController {
    
    @Autowired
    private BusinessIntelligenceService businessIntelligenceService;
    
    @GetMapping
    public String showBusinessIntelligence(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }
        
        Map<String, Object> dashboardData = businessIntelligenceService.getDashboardData();
        Map<String, Object> analytics = businessIntelligenceService.getAdvancedAnalytics();
        
        model.addAllAttributes(dashboardData);
        model.addAllAttributes(analytics);
        
        return "business-intelligence";
    }
    
    @GetMapping("/api/dashboard-data")
    @ResponseBody
    public Map<String, Object> getDashboardData() {
        return businessIntelligenceService.getDashboardData();
    }
    
    @GetMapping("/api/analytics")
    @ResponseBody
    public Map<String, Object> getAnalytics() {
        return businessIntelligenceService.getAdvancedAnalytics();
    }
    
    @GetMapping("/api/metrics/{date}")
    @ResponseBody
    public BusinessMetrics getMetricsForDate(@PathVariable String date) {
        LocalDate targetDate = LocalDate.parse(date);
        return businessIntelligenceService.getMetricsForDate(targetDate);
    }
}