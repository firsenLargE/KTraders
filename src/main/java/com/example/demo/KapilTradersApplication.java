package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import java.util.Map;
import java.util.Objects;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // <-- added
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.data.jpa.repository.Query;

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

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class KapilTradersApplication {

    public static void main(String[] args) {
        SpringApplication.run(KapilTradersApplication.class, args);
    }
}

/* ============================
   ========== ENTITIES =========
   ============================ */

@Entity
class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String contact;
    private String email;
    private String address;

    @OneToMany(mappedBy = "customer")
    private List<Sale> sales;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public List<Sale> getSales() { return sales; }
    public void setSales(List<Sale> sales) { this.sales = sales; }
    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
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

    public User() {}
    public User(String uname, String email, String password) {
        this.uname = uname;
        this.email = email;
        this.password = password;
    }

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
    private Integer minStockLevel = 10;
    private Integer maxStockLevel;
    private String supplier;

    public Product() {}
    public Product(String name, String category, String size, Integer quantity, Double price, LocalDate date, String imagePath) {
        this.name = name; this.category = category; this.size = size; this.quantity = quantity;
        this.price = price; this.date = date; this.imagePath = imagePath;
    }

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
    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }
    public Integer getMaxStockLevel() { return maxStockLevel; }
    public void setMaxStockLevel(Integer maxStockLevel) { this.maxStockLevel = maxStockLevel; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
}

@Entity
@Table(name = "sales")
class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne @JoinColumn(name = "customer_id")
    private Customer customer;

    private Integer quantity;
    private Double totalPrice;
    private LocalDate date;

    public Sale() {}
    public Sale(Product product, Integer quantity, Double totalPrice, LocalDate date) {
        this.product = product; this.quantity = quantity; this.totalPrice = totalPrice; this.date = date;
    }

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

@Entity
@Table(name = "orders")
class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private Double unitPrice;
    private Double totalAmount;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private String notes;

    public Order() {}
    public Order(Customer customer, Product product, Integer quantity, Double unitPrice, LocalDate orderDate) {
        this.customer = customer; this.product = product; this.quantity = quantity; this.unitPrice = unitPrice;
        this.totalAmount = unitPrice * quantity; this.orderDate = orderDate; this.expectedDeliveryDate = orderDate.plusDays(7);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

enum OrderStatus { PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED }

@Entity
@Table(name = "forecasts")
class Forecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "product_id")
    private Product product;

    private String season;
    private Integer predictedDemand;
    private Double confidence;
    private LocalDate forecastDate;
    private LocalDate forecastFor;
    private String method;
    private Double accuracy;

    public Forecast() {}
    public Forecast(Product product, String season, Integer predictedDemand, Double confidence, LocalDate forecastFor, String method) {
        this.product = product; this.season = season; this.predictedDemand = predictedDemand; this.confidence = confidence;
        this.forecastDate = LocalDate.now(); this.forecastFor = forecastFor; this.method = method;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }
    public Integer getPredictedDemand() { return predictedDemand; }
    public void setPredictedDemand(Integer predictedDemand) { this.predictedDemand = predictedDemand; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public LocalDate getForecastDate() { return forecastDate; }
    public void setForecastDate(LocalDate forecastDate) { this.forecastDate = forecastDate; }
    public LocalDate getForecastFor() { return forecastFor; }
    public void setForecastFor(LocalDate forecastFor) { this.forecastFor = forecastFor; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }
}

/* =============================
   ========= REPOSITORIES =======
   ============================= */

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

    @Query("SELECT p FROM Product p WHERE p.quantity <= p.minStockLevel")
    List<Product> findProductsNeedingReorder();
}

@Repository
interface SaleRepository extends org.springframework.data.jpa.repository.JpaRepository<Sale, Integer> {
    List<Sale> findByDate(LocalDate date);

    @Query("SELECT s FROM Sale s WHERE s.product.id = ?1 AND s.date BETWEEN ?2 AND ?3")
    List<Sale> findByProductAndDateRange(Integer productId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(s.quantity) FROM Sale s WHERE s.product.id = ?1 AND s.date BETWEEN ?2 AND ?3")
    Optional<Long> sumQuantityByProductAndDateRange(Integer productId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT s FROM Sale s WHERE s.date >= ?1 ORDER BY s.date DESC")
    List<Sale> findRecentSales(LocalDate fromDate);
}

@Repository
interface CustomerRepository extends org.springframework.data.jpa.repository.JpaRepository<Customer, Long> {
    List<Customer> findByNameContainingIgnoreCase(String name);
}

@Repository
interface OrderRepository extends org.springframework.data.jpa.repository.JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findAllOrderByOrderDateDesc();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = ?1")
    long countByStatus(OrderStatus status);
}

@Repository
interface ForecastRepository extends org.springframework.data.jpa.repository.JpaRepository<Forecast, Long> {
    List<Forecast> findByProductId(Integer productId);
    List<Forecast> findByForecastForBetween(LocalDate startDate, LocalDate endDate);
    List<Forecast> findBySeason(String season);

    @Query("SELECT f FROM Forecast f WHERE f.product.id = ?1 AND f.forecastFor = ?2")
    Optional<Forecast> findByProductAndForecastFor(Integer productId, LocalDate forecastFor);
}

/* =============================
   ======== SERVICE APIs ========
   ============================= */

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
    List<Product> getProductsNeedingReorder();
}

interface SaleService {
    void addSale(Sale sale);
    List<Sale> getAllSales();
    Sale getSaleById(Integer id);
    List<Sale> getRecentSales(int days);
    List<Sale> getSalesByProductAndDateRange(Integer productId, LocalDate startDate, LocalDate endDate);
    Long getTotalSalesQuantityForProduct(Integer productId, LocalDate startDate, LocalDate endDate);
}

interface CustomerService {
    void saveCustomer(Customer customer);
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    void deleteCustomer(Long id);
    List<Customer> searchCustomers(String name);
}

interface OrderService {
    void createOrder(Order order);
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    void updateOrderStatus(Long orderId, OrderStatus status);
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOrdersByCustomer(Long customerId);
    long countOrdersByStatus(OrderStatus status);
    void deleteOrder(Long id);
}

interface ForecastService {
    void generateForecast(Integer productId);
    List<Forecast> getForecastsByProduct(Integer productId);
    List<Forecast> getSeasonalForecasts(String season);
    void generateAllProductForecasts();
    Forecast getForecastForProductAndDate(Integer productId, LocalDate forecastFor);
}

/* =====================================
   ========= SERVICE IMPLEMENTATIONS ====
   ===================================== */

@Service
class UserServiceImpl implements UserService {
    @Autowired private UserRepository userRepository;
    @Override public void signUp(User user) { userRepository.save(user); }
    @Override public User login(String email, String password) { return userRepository.findByEmailAndPassword(email, password); }
    @Override public User findByEmail(String email) { return userRepository.findByEmail(email); }
    @Override public boolean existsByEmail(String email) { return userRepository.existsByEmail(email); }
}

@Service
class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Autowired public ProductServiceImpl(ProductRepository productRepository) { this.productRepository = productRepository; }
    @Override public void addProduct(Product product) { productRepository.save(product); }
    @Override public List<Product> getAllProducts() { return productRepository.findAll(); }
    @Override public Product getProductById(Integer id) { return productRepository.findById(id).orElse(null); }
    @Override public void updateProduct(Product product) { productRepository.save(product); }
    @Override public List<Product> searchProducts(String name) { return productRepository.findByNameContainingIgnoreCase(name); }
    @Override public void deleteProduct(Integer id) { productRepository.findById(id).ifPresent(productRepository::delete); }
    @Override public long countProducts() { return productRepository.count(); }
    @Override public long countLowStockProducts() { return productRepository.countByQuantityLessThan(10); }
    @Override public double getTotalInventoryValue() {
        return productRepository.findAll().stream()
                .mapToDouble(p -> (p.getPrice() != null ? p.getPrice() : 0.0) * (p.getQuantity() != null ? p.getQuantity() : 0))
                .sum();
    }
    @Override public List<Product> getProductsNeedingReorder() { return productRepository.findProductsNeedingReorder(); }
}

@Service
class SaleServiceImpl implements SaleService {
    @Autowired private SaleRepository saleRepository;
    @Override public void addSale(Sale sale) { saleRepository.save(sale); }
    @Override public List<Sale> getAllSales() { return saleRepository.findAll(); }
    @Override public Sale getSaleById(Integer id) { return saleRepository.findById(id).orElse(null); }
    @Override public List<Sale> getRecentSales(int days) {
        LocalDate fromDate = LocalDate.now().minusDays(days);
        return saleRepository.findRecentSales(fromDate);
    }
    @Override public List<Sale> getSalesByProductAndDateRange(Integer productId, LocalDate startDate, LocalDate endDate) {
        return saleRepository.findByProductAndDateRange(productId, startDate, endDate);
    }
    @Override public Long getTotalSalesQuantityForProduct(Integer productId, LocalDate startDate, LocalDate endDate) {
        return saleRepository.sumQuantityByProductAndDateRange(productId, startDate, endDate).orElse(0L);
    }
}

@Service
class CustomerServiceImpl implements CustomerService {
    @Autowired private CustomerRepository customerRepository;
    @Override public void saveCustomer(Customer customer) { customerRepository.save(customer); }
    @Override public List<Customer> getAllCustomers() { return customerRepository.findAll(); }
    @Override public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
    }
    @Override public void deleteCustomer(Long id) { customerRepository.deleteById(id); }
    @Override public List<Customer> searchCustomers(String name) { return customerRepository.findByNameContainingIgnoreCase(name); }
}

@Service
class OrderServiceImpl implements OrderService {
    @Autowired private OrderRepository orderRepository;
    @Override public void createOrder(Order order) {
        if (order.getTotalAmount() == null && order.getUnitPrice() != null && order.getQuantity() != null) {
            order.setTotalAmount(order.getUnitPrice() * order.getQuantity());
        }
        orderRepository.save(order);
    }
    @Override public List<Order> getAllOrders() { return orderRepository.findAllOrderByOrderDateDesc(); }
    @Override public Order getOrderById(Long id) { return orderRepository.findById(id).orElse(null); }
    @Override public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) { order.setStatus(status); orderRepository.save(order); }
    }
    @Override public List<Order> getOrdersByStatus(OrderStatus status) { return orderRepository.findByStatus(status); }
    @Override public List<Order> getOrdersByCustomer(Long customerId) { return orderRepository.findByCustomerId(customerId); }
    @Override public long countOrdersByStatus(OrderStatus status) { return orderRepository.countByStatus(status); }
    @Override public void deleteOrder(Long id) { orderRepository.deleteById(id); }
}

@Service
class ForecastServiceImpl implements ForecastService {
    @Autowired private ForecastRepository forecastRepository;
    @Autowired private SaleService saleService;
    @Autowired private ProductService productService;

    @Override
    public void generateForecast(Integer productId) {
        Product product = productService.getProductById(productId);
        if (product == null) return;

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(12);
        List<Sale> historicalSales = saleService.getSalesByProductAndDateRange(productId, startDate, endDate);

        for (int i = 1; i <= 3; i++) {
            LocalDate forecastDate = endDate.plusMonths(i);
            String season = getSeason(forecastDate);
            Integer predictedDemand = calculateSeasonalDemand(historicalSales);
            Double confidence = calculateConfidence(historicalSales);

            Optional<Forecast> existing = forecastRepository.findByProductAndForecastFor(productId, forecastDate.withDayOfMonth(1));
            Forecast forecast = existing.orElseGet(() -> new Forecast(product, season, predictedDemand, confidence, forecastDate.withDayOfMonth(1), "SEASONAL"));
            forecast.setPredictedDemand(predictedDemand);
            forecast.setConfidence(confidence);
            forecast.setSeason(season);
            forecastRepository.save(forecast);
        }
    }

    private String getSeason(LocalDate date) {
        switch (date.getMonth()) {
            case DECEMBER: case JANUARY: case FEBRUARY: return "WINTER";
            case MARCH: case APRIL: case MAY: return "SPRING";
            case JUNE: case JULY: case AUGUST: return "SUMMER";
            case SEPTEMBER: case OCTOBER: case NOVEMBER: return "AUTUMN";
            default: return "UNKNOWN";
        }
    }

    private Integer calculateSeasonalDemand(List<Sale> historicalSales) {
        if (historicalSales.isEmpty()) return 5;
        double avg = historicalSales.stream().mapToInt(Sale::getQuantity).average().orElse(5.0);
        double trend = 1.0 + (Math.random() * 0.2 - 0.1);
        return Math.max(1, (int) Math.ceil(avg * trend));
        }

    private Double calculateConfidence(List<Sale> historicalSales) {
        if (historicalSales.size() < 3) return 0.6;
        double mean = historicalSales.stream().mapToInt(Sale::getQuantity).average().orElse(1.0);
        double variance = historicalSales.stream()
            .mapToDouble(s -> Math.pow(s.getQuantity() - mean, 2))
            .average().orElse(1.0);
        double stdDev = Math.sqrt(variance);
        double cv = stdDev / mean;
        double confidence = Math.max(0.5, 1.0 - cv);
        return BigDecimal.valueOf(confidence).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override public List<Forecast> getForecastsByProduct(Integer productId) { return forecastRepository.findByProductId(productId); }
    @Override public List<Forecast> getSeasonalForecasts(String season) { return forecastRepository.findBySeason(season); }
    @Override public void generateAllProductForecasts() { productService.getAllProducts().forEach(p -> generateForecast(p.getId())); }
    @Override public Forecast getForecastForProductAndDate(Integer productId, LocalDate forecastFor) {
        return forecastRepository.findByProductAndForecastFor(productId, forecastFor).orElse(null);
    }
}

/* =====================================
   ============= CONTROLLERS ============
   ===================================== */

@Controller
class HomeController {
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

        List<Product> recentProducts = products.stream()
            .sorted((p1, p2) -> p2.getDate() != null && p1.getDate() != null ? p2.getDate().compareTo(p1.getDate()) : 0)
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
        model.addAttribute("productQuantities", products.stream().map(p -> p.getQuantity() != null ? p.getQuantity() : 0).toList());
        model.addAttribute("categoryCounts", products.stream().collect(Collectors.groupingBy(Product::getCategory, Collectors.counting())));
        return "reports";
    }

    @GetMapping("/signup")
    public String registerForm(Model model) { model.addAttribute("user", new User()); return "signup"; }

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

    @GetMapping("/logout")
    public String logout(HttpSession session) { session.invalidate(); return "redirect:/"; }

    @PostMapping("/signup")
    public String postSignup(@ModelAttribute User user, RedirectAttributes redirectAttributes, Model model) {
        try {
            if (userService.existsByEmail(user.getEmail().toLowerCase())) {
                model.addAttribute("error", "Email already exists.");
                return "signup";
            }
            user.setEmail(user.getEmail().toLowerCase());
            userService.signUp(user);
            redirectAttributes.addFlashAttribute("success", "Account created successfully!");
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Error creating account: " + e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/api/products")
    @ResponseBody
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
                m.put("ratio", price > 0 ? String.format("%.2f", (double)value / price) : "0.00");
                return m;
            }).toList();
            return ResponseEntity.ok(productsJson);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> getStatistics(HttpSession session) {
        if (session.getAttribute("validuser") == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        try {
            List<Product> products = productService.getAllProducts();
            Map<String,Object> stats = new HashMap<>();
            if (products == null || products.isEmpty()) {
                stats.put("totalProducts", 0);
                stats.put("avgPrice", 0);
                stats.put("totalValue", 0);
                stats.put("categoriesCount", 0);
            } else {
                stats.put("totalProducts", products.size());
                double avgPrice = products.stream().filter(p -> p.getPrice() != null).mapToDouble(Product::getPrice).average().orElse(0.0);
                stats.put("avgPrice", Math.round(avgPrice));
                double totalValue = products.stream().mapToDouble(p ->
                    (p.getPrice() != null ? p.getPrice() : 0.0) * (p.getQuantity() != null ? p.getQuantity() : 0)
                ).sum();
                stats.put("totalValue", Math.round(totalValue));
                long categoriesCount = products.stream().map(Product::getCategory).filter(Objects::nonNull).distinct().count();
                stats.put("categoriesCount", categoriesCount);
            }
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // helper used by /api/products
    private int calculateProductValue(Product p, int price, int stock) {
        double stockFactor = Math.min(Math.max(stock, 0), 100) / 100.0;
        double catFactor = 0.0;
        String cat = (p.getCategory() != null) ? p.getCategory().toLowerCase() : "";
        switch (cat) {
            case "electronics": catFactor = 0.12; break;
            case "premium":     catFactor = 0.10; break;
            case "bestseller":  catFactor = 0.08; break;
            case "gadgets":     catFactor = 0.05; break;
            default:            catFactor = 0.00;
        }
        double v = price * (1.0 + catFactor + 0.20 * stockFactor);
        return Math.max(1, (int)Math.round(v));
    }
}

@Controller
class ProductController {
    @Autowired private ProductService productService;

    @GetMapping("/products")
    public String showProducts(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        populateSummary(model, products);
        return "products";
    }

    @GetMapping("/add-product")
    public String showAddProductForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("product", new Product());
        return "add-product";
    }

    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            if (product.getDate() == null) product.setDate(LocalDate.now());
            String uploadDir = "uploads/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) uploadPath.mkdirs();
            if (!imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImagePath("/uploads/" + fileName);
            }
            productService.addProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product added!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/edit-product/{id}")
    public String showEditProductForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        Product product = productService.getProductById(id);
        if (product == null) return "redirect:/products";
        model.addAttribute("product", product);
        return "edit-product";
    }

    @PostMapping("/edit-product/{id}")
    public String updateProduct(@PathVariable Integer id, @ModelAttribute Product product, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            product.setId(id);
            productService.updateProduct(product);
            redirectAttributes.addFlashAttribute("success", "Updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
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
        long lowStock = products.stream().filter(p -> (p.getQuantity() != null ? p.getQuantity() : 0) < 10).count();
        model.addAttribute("lowStockProducts", lowStock);
        double totalValue = products.stream().mapToDouble(p ->
            (p.getPrice() != null ? p.getPrice() : 0.0) * (p.getQuantity() != null ? p.getQuantity() : 0)
        ).sum();
        model.addAttribute("totalValue", totalValue);
    }
}

@Controller
@RequestMapping("/optimizer")
 class OptimizerController {

    @Autowired private BudgetPlannerService budgetPlannerService;

    @GetMapping
    public String showOptimizer(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("strategies", ReplenishmentStrategy.values());
        return "optimizer"; // src/main/resources/templates/optimizer.html
    }

@PostMapping("/plan")
@ResponseBody
public ResponseEntity<PurchasePlanDTO> planPurchases(@RequestParam int budget,
                                                     @RequestParam ReplenishmentStrategy strategy,
                                                     @RequestParam(defaultValue = "false") boolean persist,
                                                     @RequestParam(name = "selectedIds", required = false) java.util.Set<Integer> selectedIds,
                                                     HttpSession session) {
    if (session.getAttribute("validuser") == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    PurchasePlanDTO plan = budgetPlannerService.planPurchases(budget, strategy, persist, selectedIds);
    return ResponseEntity.ok(plan);
}





    // TEMP probe: remove after testing
    @GetMapping("/ping")
    @ResponseBody
    public String ping() { return "ok"; }
}
@Controller
class SaleController {
    @Autowired private SaleService saleService;
    @Autowired private ProductService productService;
    @Autowired private CustomerService customerService;

    @GetMapping("/sales")
    public String showSalesForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("sale", new Sale());
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
    public String addSale(@ModelAttribute Sale sale, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            Product product = productService.getProductById(sale.getProduct().getId());
            Customer customer = customerService.getCustomerById(sale.getCustomer().getId());
            if (product == null || sale.getQuantity() > product.getQuantity()) {
                redirectAttributes.addFlashAttribute("error", "Invalid product or insufficient stock");
                return "redirect:/sales";
            }
            sale.setProduct(product);
            sale.setCustomer(customer);
            product.setQuantity(product.getQuantity() - sale.getQuantity());
            productService.updateProduct(product);
            sale.setTotalPrice(product.getPrice() * sale.getQuantity());
            sale.setDate(LocalDate.now());
            saleService.addSale(sale);
            redirectAttributes.addFlashAttribute("success", "Sale added!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/sales";
    }



@GetMapping("/sales/{id}/receipt")
public void generateReceipt(@PathVariable Integer id,
                            HttpServletResponse response) throws IOException, DocumentException {

    Sale sale = saleService.getSaleById(id);
    if (sale == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Sale not found");
        return;
    }

    response.reset();
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=receipt_" + id + ".pdf");

    Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
    boolean wroteSomething = false;

    try {
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        // === colors & fonts ===
        BaseColor brand = new BaseColor(26, 95, 45);
        BaseColor pale  = new BaseColor(249, 250, 240);
        Font titleFont  = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, brand);
        Font hFont      = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font labelFont  = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
        Font textFont   = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        Font strongFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);

        // === outer panel ===
        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(94);
        PdfPCell panel = new PdfPCell();
        panel.setPadding(14f);
        panel.setBorderColor(new BaseColor(170, 190, 160));
        panel.setBorderWidth(1.2f);
        panel.setBackgroundColor(pale);

        // === header ===
        PdfPTable header = new PdfPTable(new float[]{1f});
        header.setWidthPercentage(100);

        Paragraph store = new Paragraph("Kapil Traders", titleFont);
        store.setAlignment(Element.ALIGN_CENTER);
        Paragraph inv = new Paragraph("Invoice", hFont);
        inv.setAlignment(Element.ALIGN_CENTER);
        Paragraph date = new Paragraph(
                "Date: " + (sale.getDate() != null ? sale.getDate() : java.time.LocalDate.now()),
                textFont);
        date.setAlignment(Element.ALIGN_CENTER);

        PdfPCell c1 = new PdfPCell();
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPaddingTop(4f);
        c1.addElement(store);
        c1.addElement(new Paragraph(" ", textFont));
        c1.addElement(inv);
        c1.addElement(date);
        header.addCell(c1);
        panel.addElement(header);

        // === key/value table ===
        PdfPTable kv = new PdfPTable(new float[]{2.2f, 5.8f});
        kv.setWidthPercentage(100);
        kv.setSpacingBefore(12f);

        java.util.function.BiConsumer<String,String> addRow = (label, value) -> {
            PdfPCell l = new PdfPCell(new Phrase(label, labelFont));
            l.setBorder(Rectangle.NO_BORDER);
            l.setPaddingTop(6f); l.setPaddingBottom(6f);
            PdfPCell v = new PdfPCell(new Phrase(value != null ? value : "", textFont));
            v.setBorder(Rectangle.NO_BORDER);
            v.setPaddingTop(6f); v.setPaddingBottom(6f);
            v.setBackgroundColor(BaseColor.WHITE);
            kv.addCell(l); kv.addCell(v);
        };

        String customerName  = sale.getCustomer() != null ? sale.getCustomer().getName()   : "Walk-in";
        String customerPhone = sale.getCustomer() != null ? sale.getCustomer().getContact() : "-";
        String itemName = sale.getProduct() != null ? sale.getProduct().getName() : "-";
        int qty = sale.getQuantity() != null ? sale.getQuantity() : 0;
        double unit = (sale.getProduct() != null && sale.getProduct().getPrice() != null)
                      ? sale.getProduct().getPrice() : 0.0;
        double total = sale.getTotalPrice() != null ? sale.getTotalPrice() : unit * qty;

        addRow.accept("Customer", customerName);
        addRow.accept("Phone", customerPhone);
        addRow.accept("Item", itemName);
        addRow.accept("Quantity", String.valueOf(qty));
        addRow.accept("Price (Per Unit)", "Rs. " + String.format("%.2f", unit));

        PdfPCell lTot = new PdfPCell(new Phrase("Total", labelFont));
        lTot.setBorder(Rectangle.NO_BORDER); lTot.setPaddingTop(8f); lTot.setPaddingBottom(8f);
        PdfPCell vTot = new PdfPCell(new Phrase("Rs. " + String.format("%.2f", total), strongFont));
        vTot.setBorder(Rectangle.NO_BORDER); vTot.setPaddingTop(8f); vTot.setPaddingBottom(8f);
        kv.addCell(lTot); kv.addCell(vTot);

        panel.addElement(kv);

        // === note & policy ===
        panel.addElement(new Paragraph("Was a good business with you!", hFont));
        panel.addElement(new Paragraph("Thanking You!", textFont));

        Paragraph polTitle = new Paragraph("Return & Exchange Policy:", hFont);
        polTitle.setSpacingBefore(10f);
        panel.addElement(polTitle);

        com.itextpdf.text.List policy = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
        policy.setIndentationLeft(14f);
        policy.add(new ListItem("✔ Breakage and Leakage fully refund.", textFont));
        policy.add(new ListItem("✔ Invoice is mandatory for all returns", textFont));
        policy.add(new ListItem("✘ Change of mind is not accepted", textFont));
        panel.addElement(policy);

        // === contact + QR ===
        PdfPTable contact = new PdfPTable(new float[]{3.8f, 2.2f});
        contact.setWidthPercentage(100);
        contact.setSpacingBefore(8f);

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.addElement(new Paragraph("Need Help? Contact Us:", labelFont));
        left.addElement(new Paragraph("www.kapiltradersbusiness.com", textFont));
        left.addElement(new Paragraph("Customer Care: 9815348524", textFont));
        contact.addCell(left);

        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        right.setPadding(6f);

        // Try to load QR from classpath:/static/qr.jpg
        boolean qrAdded = false;
        try {
            ClassPathResource res = new ClassPathResource("static/uploads/qr.jpg");
            if (res.exists()) {
                byte[] bytes = res.getContentAsByteArray();
                Image qr = Image.getInstance(bytes);
                qr.scaleToFit(110, 110);
                right.addElement(qr);
                qrAdded = true;
            }
        } catch (Exception ignore) {
            // fall back to placeholder
        }

        if (!qrAdded) {
            PdfPTable qrBox = new PdfPTable(1);
            qrBox.setWidthPercentage(45);
            PdfPCell qrCell = new PdfPCell(new Phrase("QR CODE", labelFont));
            qrCell.setFixedHeight(110f);
            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            qrCell.setBorderColor(new BaseColor(210, 210, 210));
            qrBox.addCell(qrCell);
            right.addElement(qrBox);
        }

        contact.addCell(right);
        panel.addElement(contact);

        panel.addElement(new Paragraph("Scan to reach us on Instagram", textFont));

        wrapper.addCell(panel);
        doc.add(wrapper);
        wroteSomething = true;

    } catch (Exception ex) {
        if (doc.isOpen() && !wroteSomething) {
            doc.add(new Paragraph(" "));   // ensure at least one page exists
            wroteSomething = true;
        }
        throw new IOException("Failed to generate receipt PDF", ex);
    } finally {
        if (doc.isOpen()) doc.close();
    }
}



}

@Controller
@RequestMapping("/customers")
class CustomerController {
    @Autowired private CustomerService customerService;

    @GetMapping
    public String listCustomers(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("customer", new Customer());
        return "customers";
    }
        @GetMapping("/add")
    public String showAddCustomer(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("customer", new Customer());
        return "customers"; // same template with the form on it
    }

    @PostMapping("/add")
    public String saveOrUpdateCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            customerService.saveCustomer(customer);
            redirectAttributes.addFlashAttribute("success", customer.getId() == null ? "Customer added!" : "Customer updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        Customer customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("success", "Customer deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/customers";
    }
}

@Controller
@RequestMapping("/orders")
class OrderController {
    @Autowired private OrderService orderService;
    @Autowired private CustomerService customerService;
    @Autowired private ProductService productService;

    @GetMapping
    public String listOrders(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("order", new Order());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("pendingCount", orderService.countOrdersByStatus(OrderStatus.PENDING));
        model.addAttribute("processingCount", orderService.countOrdersByStatus(OrderStatus.PROCESSING));
        model.addAttribute("shippedCount", orderService.countOrdersByStatus(OrderStatus.SHIPPED));
        model.addAttribute("deliveredCount", orderService.countOrdersByStatus(OrderStatus.DELIVERED));
        return "orders";
    }

    @PostMapping("/add")
    public String createOrder(@ModelAttribute Order order, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            Customer customer = customerService.getCustomerById(order.getCustomer().getId());
            Product product = productService.getProductById(order.getProduct().getId());
            if (customer == null || product == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid customer or product");
                return "redirect:/orders";
            }
            order.setCustomer(customer);
            order.setProduct(product);
            order.setUnitPrice(product.getPrice());
            order.setOrderDate(LocalDate.now());
            orderService.createOrder(order);
            redirectAttributes.addFlashAttribute("success", "Order created!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/orders";
    }

    @PostMapping("/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/orders";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            orderService.deleteOrder(id);
            redirectAttributes.addFlashAttribute("success", "Order deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/orders";
    }
}

@Controller
@RequestMapping("/forecasts")
class ForecastController {
    @Autowired private ForecastService forecastService;
    @Autowired private ProductService productService;

    @GetMapping
    public String showForecasts(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            List<Product> products = productService.getAllProducts();
            Map<Integer, List<Forecast>> productForecasts = new HashMap<>();
            for (Product p : products) {
                try {
                    productForecasts.put(p.getId(), forecastService.getForecastsByProduct(p.getId()));
                } catch (Exception e) {
                    productForecasts.put(p.getId(), new ArrayList<>());
                }
            }
            int totalForecasts = productForecasts.values().stream().mapToInt(List::size).sum();
            model.addAttribute("products", products);
            model.addAttribute("productForecasts", productForecasts);
            model.addAttribute("seasons", List.of("SPRING", "SUMMER", "AUTUMN", "WINTER"));
            model.addAttribute("totalProducts", products.size());
            model.addAttribute("totalForecasts", totalForecasts);
        } catch (Exception e) {
            model.addAttribute("error", "Error loading forecasts");
        }
        return "forecasts";
    }

    @PostMapping("/generate/{productId}")
    public String generateForecast(@PathVariable Integer productId, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Product not found!");
                return "redirect:/forecasts";
            }
            forecastService.generateForecast(productId);
            redirectAttributes.addFlashAttribute("success", "Forecast generated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/forecasts";
    }

    @PostMapping("/generate-all")
    public String generateAllForecasts(RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null) return "redirect:/";
        try {
            List<Product> products = productService.getAllProducts();
            if (products.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No products found!");
                return "redirect:/forecasts";
            }
            int successCount = 0, errorCount = 0;
            for (Product product : products) {
                try { forecastService.generateForecast(product.getId()); successCount++; }
                catch (Exception e) { errorCount++; }
            }
            if (successCount > 0)
                redirectAttributes.addFlashAttribute("success", "Forecasts generated for " + successCount + " products, errors: " + errorCount);
            else
                redirectAttributes.addFlashAttribute("error", "No forecasts generated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/forecasts";
    }
}

/* =====================================
   ======== DATA INITIALIZER ============
   ===================================== */

@Component
class DataInitializer implements CommandLineRunner {
    @Autowired private UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User adminUser = new User("admin", "admin@kapiltraders.com", "admin123");
            userRepository.save(adminUser);
            System.out.println("Created default admin user: admin@kapiltraders.com / admin123");
        }
    }
}

/* =====================================
   ======== Algorithm part ============
   ===================================== */

enum ReplenishmentStrategy {
    ROUND_ROBIN,      // spread spend: 1 unit per item in priority order, loop
    LOW_STOCK_FIRST,  // strictly fill biggest gap to min first, then toward max
    BEST_VALUE        // (gap + category weight) per rupee, greedy to max
}


class PurchasePlanItemDTO {
    public Integer productId;
    public String productName;
    public int units;
    public int unitPrice;
    public int lineCost;

    public PurchasePlanItemDTO(Integer productId, String productName, int units, int unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.units = units;
        this.unitPrice = unitPrice;
        this.lineCost = units * unitPrice;
    }
}

class PurchasePlanDTO {
    public int requestedBudget;
    public int totalCost;
    public int remaining;
    public int totalUnits;
    public ReplenishmentStrategy strategy;
    public List<PurchasePlanItemDTO> items = new ArrayList<>();
}

@Entity
@Table(name = "replenishment_plans")
class ReplenishmentPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer requestedBudget;
    private Integer totalCost;
    private Integer totalUnits;
    private String strategy; // store enum name
    private LocalDate createdAt = LocalDate.now();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplenishmentLine> lines = new ArrayList<>();

    public Long getId() { return id; }
    public Integer getRequestedBudget() { return requestedBudget; }
    public void setRequestedBudget(Integer requestedBudget) { this.requestedBudget = requestedBudget; }
    public Integer getTotalCost() { return totalCost; }
    public void setTotalCost(Integer totalCost) { this.totalCost = totalCost; }
    public Integer getTotalUnits() { return totalUnits; }
    public void setTotalUnits(Integer totalUnits) { this.totalUnits = totalUnits; }
    public String getStrategy() { return strategy; }
    public void setStrategy(String strategy) { this.strategy = strategy; }
    public LocalDate getCreatedAt() { return createdAt; }
    public List<ReplenishmentLine> getLines() { return lines; }
    public void setLines(List<ReplenishmentLine> lines) { this.lines = lines; }
}

@Entity
@Table(name = "replenishment_lines")
class ReplenishmentLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "plan_id")
    private ReplenishmentPlan plan;

    @ManyToOne @JoinColumn(name = "product_id")
    private Product product;

    private Integer units;
    private Integer unitPrice;
    private Integer lineCost;

    public Long getId() { return id; }
    public ReplenishmentPlan getPlan() { return plan; }
    public void setPlan(ReplenishmentPlan plan) { this.plan = plan; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getUnits() { return units; }
    public void setUnits(Integer units) { this.units = units; }
    public Integer getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Integer unitPrice) { this.unitPrice = unitPrice; }
    public Integer getLineCost() { return lineCost; }
    public void setLineCost(Integer lineCost) { this.lineCost = lineCost; }
}

@Repository
interface ReplenishmentPlanRepository extends org.springframework.data.jpa.repository.JpaRepository<ReplenishmentPlan, Long> { }

@Repository
interface ReplenishmentLineRepository extends org.springframework.data.jpa.repository.JpaRepository<ReplenishmentLine, Long> { }

interface BudgetPlannerService {
    PurchasePlanDTO planPurchases(int budget,
                                  ReplenishmentStrategy strategy,
                                  boolean persistPlan,
                                  java.util.Set<Integer> includeProductIds);
}




@Service
class BudgetPlannerServiceImpl implements BudgetPlannerService {

    @Autowired private ProductRepository productRepository;
    @Autowired(required = false) private ReplenishmentPlanRepository planRepo;

    /** Visible to all helper methods below */
    private static final class Cand {
        Product p;
        int price;
        int qty;
        int minLvl;
        int maxLvl;
        int gapToMin;
        double catW;
        double priority;
    }

    @Override
    @Transactional
    public PurchasePlanDTO planPurchases(int budget,
                                         ReplenishmentStrategy strategy,
                                         boolean persistPlan,
                                         java.util.Set<Integer> includeProductIds) {
        PurchasePlanDTO out = new PurchasePlanDTO();
        out.requestedBudget = Math.max(0, budget);
        out.strategy = strategy;
        if (budget <= 0) return out;

        // 1) Load candidates
        List<Product> all = productRepository.findAll();

        List<Product> eligible = all.stream()
            .filter(p -> p.getPrice() != null && p.getPrice() > 0)
            .filter(p -> {
                Integer q = p.getQuantity();
                Integer mx = p.getMaxStockLevel();
                return mx == null || q == null || q < mx;
            })
            .collect(Collectors.toList());

        if (includeProductIds != null && !includeProductIds.isEmpty()) {
            eligible = eligible.stream()
                    .filter(p -> includeProductIds.contains(p.getId()))
                    .collect(Collectors.toList());
        }
        if (eligible.isEmpty()) return out;

        // 2) Build scored view
        List<Cand> scored = new ArrayList<>();
        for (Product p : eligible) {
            Cand c = new Cand();
            c.p = p;
            c.price = p.getPrice().intValue();
            c.qty = p.getQuantity() != null ? p.getQuantity() : 0;
            c.minLvl = p.getMinStockLevel() != null ? p.getMinStockLevel() : 0;
            c.maxLvl = p.getMaxStockLevel() != null ? p.getMaxStockLevel() : Integer.MAX_VALUE;
            c.gapToMin = Math.max(0, c.minLvl - c.qty);
            String cat = (p.getCategory() == null ? "" : p.getCategory().toLowerCase());
            c.catW = switch (cat) {
                case "electronics" -> 0.12;
                case "premium"     -> 0.10;
                case "bestseller"  -> 0.08;
                case "gadgets"     -> 0.05;
                default            -> 0.00;
            };
            scored.add(c);
        }

        // 3) Run chosen strategy
        Map<Integer,Integer> bought = switch (strategy) {
            case ROUND_ROBIN     -> purchaseRoundRobin(scored, budget);
            case LOW_STOCK_FIRST -> purchaseLowStockFirst(scored, budget);
            case BEST_VALUE      -> purchaseBestValue(scored, budget);
            // If you still use the old enum names, map them like:
            // case MAX_UNITS       -> purchaseBestValue(scored, budget); // cheapest/value
            // case AVOID_STOCKOUTS -> purchaseLowStockFirst(scored, budget);
            // case HYBRID          -> purchaseRoundRobin(scored, budget);
        };

        // 4) Build DTO
        for (var e : bought.entrySet()) {
            Integer pid = e.getKey();
            int units = e.getValue();
            if (units <= 0) continue;
            Product p = all.stream().filter(pp -> Objects.equals(pp.getId(), pid)).findFirst().orElse(null);
            if (p == null) continue;
            int unitPrice = p.getPrice().intValue();
            out.items.add(new PurchasePlanItemDTO(pid, p.getName(), units, unitPrice));
        }
        out.totalCost = out.items.stream().mapToInt(i -> i.lineCost).sum();
        out.remaining = Math.max(0, budget - out.totalCost);
        out.totalUnits = out.items.stream().mapToInt(i -> i.units).sum();

        // 5) Optional persistence
        if (persistPlan && planRepo != null && !out.items.isEmpty()) {
            ReplenishmentPlan plan = new ReplenishmentPlan();
            plan.setRequestedBudget(budget);
            plan.setTotalCost(out.totalCost);
            plan.setTotalUnits(out.totalUnits);
            plan.setStrategy(strategy.name());

            List<ReplenishmentLine> lines = new ArrayList<>();
            for (PurchasePlanItemDTO dto : out.items) {
                Product p = all.stream().filter(pp -> Objects.equals(pp.getId(), dto.productId)).findFirst().orElse(null);
                if (p == null) continue;
                ReplenishmentLine line = new ReplenishmentLine();
                line.setPlan(plan);
                line.setProduct(p);
                line.setUnits(dto.units);
                line.setUnitPrice(dto.unitPrice);
                line.setLineCost(dto.lineCost);
                lines.add(line);
            }
            plan.setLines(lines);
            planRepo.save(plan);
        }

        return out;
    }

    /* ----------------- Strategy engines ----------------- */

    // 1) ROUND_ROBIN: buy 1 per item in priority order, loop
    private Map<Integer,Integer> purchaseRoundRobin(List<Cand> scored, int budget) {
        // priority = (gap + 100*catW + 1) / price
        scored.forEach(c -> c.priority = (c.gapToMin + 100*c.catW + 1.0) / Math.max(1, c.price));
        scored.sort((a,b) -> {
            int cmp = Double.compare(b.priority, a.priority);
            return (cmp != 0) ? cmp : Integer.compare(a.price, b.price);
        });

        Map<Integer,Integer> bought = new HashMap<>();
        int remaining = budget;
        boolean purchased;
        do {
            purchased = false;
            for (Cand c : scored) {
                int have = bought.getOrDefault(c.p.getId(), 0);
                int currentQty = c.qty + have;
                if (currentQty >= c.maxLvl) continue;
                if (c.price > remaining) continue;
                remaining -= c.price;
                bought.merge(c.p.getId(), 1, Integer::sum);
                purchased = true;
            }
        } while (purchased);
        return bought;
    }

    // 2) LOW_STOCK_FIRST: fill to min level by largest gap, then toward max
    private Map<Integer,Integer> purchaseLowStockFirst(List<Cand> scored, int budget) {
        Map<Integer,Integer> bought = new HashMap<>();
        int remaining = budget;

        List<Cand> needingMin = scored.stream()
                .filter(c -> c.gapToMin > 0)
                .sorted((a,b) -> {
                    int cmp = Integer.compare(b.gapToMin, a.gapToMin);
                    return (cmp != 0) ? cmp : Integer.compare(a.price, b.price);
                })
                .toList();

        // Phase A: reach min
        for (Cand c : needingMin) {
            while (remaining >= c.price) {
                int have = bought.getOrDefault(c.p.getId(), 0);
                int currentQty = c.qty + have;
                if (currentQty >= c.minLvl) break;
                if (currentQty >= c.maxLvl) break;
                remaining -= c.price;
                bought.merge(c.p.getId(), 1, Integer::sum);
            }
            if (remaining < 1) break;
        }
        if (remaining < 1) return bought;

        // Phase B: continue to max in same order
        for (Cand c : needingMin) {
            while (remaining >= c.price) {
                int have = bought.getOrDefault(c.p.getId(), 0);
                int currentQty = c.qty + have;
                if (currentQty >= c.maxLvl) break;
                remaining -= c.price;
                bought.merge(c.p.getId(), 1, Integer::sum);
            }
            if (remaining < 1) break;
        }
        return bought;
    }

    // 3) BEST_VALUE: (gap + category weight) per rupee, greedy to max
    private Map<Integer,Integer> purchaseBestValue(List<Cand> scored, int budget) {
        scored.forEach(c -> c.priority = (c.gapToMin + 100*c.catW + 1.0) / Math.max(1, c.price));
        scored.sort((a,b) -> {
            int cmp = Double.compare(b.priority, a.priority);
            return (cmp != 0) ? cmp : Integer.compare(a.price, b.price);
        });

        Map<Integer,Integer> bought = new HashMap<>();
        int remaining = budget;
        for (Cand c : scored) {
            while (remaining >= c.price) {
                int have = bought.getOrDefault(c.p.getId(), 0);
                int currentQty = c.qty + have;
                if (currentQty >= c.maxLvl) break;
                remaining -= c.price;
                bought.merge(c.p.getId(), 1, Integer::sum);
            }
            if (remaining < scored.stream().mapToInt(k -> k.price).min().orElse(Integer.MAX_VALUE)) break;
        }
        return bought;
    }
}
