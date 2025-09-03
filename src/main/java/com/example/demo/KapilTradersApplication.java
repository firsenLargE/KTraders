package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
import java.time.Month;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@SpringBootApplication
public class KapilTradersApplication {
    public static void main(String[] args) {
        SpringApplication.run(KapilTradersApplication.class, args);
    }
}

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
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

    public User() {
    }

    public User(String uname, String email, String password) {
        this.uname = uname;
        this.email = email;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
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
    private Integer maxStockLevel = 100;
    private String supplier;

    public Product() {
    }

    public Product(String name, String category, String size, Integer quantity, Double price, LocalDate date,
            String imagePath) {
        this.name = name;
        this.category = category;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
        this.imagePath = imagePath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public Integer getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(Integer maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
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

    public Sale() {
    }

    public Sale(Product product, Integer quantity, Double totalPrice, LocalDate date) {
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}

@Entity
@Table(name = "orders")
class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private Double unitPrice;
    private Double totalAmount;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private String notes;

    public Order() {
    }

    public Order(Customer customer, Product product, Integer quantity, Double unitPrice, LocalDate orderDate) {
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = unitPrice * quantity;
        this.orderDate = orderDate;
        this.expectedDeliveryDate = orderDate.plusDays(7);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

enum OrderStatus {
    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

@Entity
@Table(name = "forecasts")
class Forecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String season;
    private Integer predictedDemand;
    private Double confidence;
    private LocalDate forecastDate;
    private LocalDate forecastFor;
    private String method;
    private Double accuracy;

    public Forecast() {
    }

    public Forecast(Product product, String season, Integer predictedDemand, Double confidence, LocalDate forecastFor,
            String method) {
        this.product = product;
        this.season = season;
        this.predictedDemand = predictedDemand;
        this.confidence = confidence;
        this.forecastDate = LocalDate.now();
        this.forecastFor = forecastFor;
        this.method = method;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public Integer getPredictedDemand() {
        return predictedDemand;
    }

    public void setPredictedDemand(Integer predictedDemand) {
        this.predictedDemand = predictedDemand;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public LocalDate getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(LocalDate forecastDate) {
        this.forecastDate = forecastDate;
    }

    public LocalDate getForecastFor() {
        return forecastFor;
    }

    public void setForecastFor(LocalDate forecastFor) {
        this.forecastFor = forecastFor;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }
}

class SimpleKnapsackProduct {
    public Integer id;
    public String name;
    public String category;
    public int price;
    public int value;
    public int stock;
    public boolean selected = false;

    public SimpleKnapsackProduct(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.category = product.getCategory();
        this.price = product.getPrice() != null ? product.getPrice().intValue() : 0;
        this.stock = product.getQuantity() != null ? product.getQuantity() : 0;
        this.value = calculateProductValue(product);
    }

    private int calculateProductValue(Product product) {
        int baseValue = this.price;
        int stockBonus = Math.min(this.stock * 2, baseValue / 4);
        int categoryBonus = getCategoryBonus(product.getCategory(), baseValue);
        return baseValue + stockBonus + categoryBonus;
    }

    private int getCategoryBonus(String category, int baseValue) {
        if (category == null)
            return 0;

        switch (category.toLowerCase()) {
            case "electronics":
                return baseValue / 10;
            case "premium":
                return baseValue / 8;
            case "bestseller":
                return baseValue / 6;
            case "gadgets":
                return baseValue / 12;
            default:
                return 0;
        }
    }
}

 class SimpleKnapsackResult {
    private List<SimpleKnapsackProduct> selectedProducts = new ArrayList<>();
    private int totalCost = 0;
    private int totalValue = 0;
    private int totalItems = 0;
    private int remainingBudget = 0;
    private double efficiencyPercent = 0.0;
    private boolean empty = false;
    private int originalBudget;

    // Getters
    public List<SimpleKnapsackProduct> getSelectedProducts() {
        return selectedProducts;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getTotalValue() {
        return totalValue;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getRemainingBudget() {
        return remainingBudget;
    }

    public double getEfficiencyPercent() {
        return efficiencyPercent;
    }

    public boolean isEmpty() {
        return empty || selectedProducts.isEmpty();
    }

    public int getOriginalBudget() {
        return originalBudget;
    }

    // Setters
    public void setSelectedProducts(List<SimpleKnapsackProduct> selectedProducts) {
        this.selectedProducts = selectedProducts;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public void setTotalValue(int totalValue) {
        this.totalValue = totalValue;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public void setRemainingBudget(int remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public void setEfficiencyPercent(double efficiencyPercent) {
        this.efficiencyPercent = efficiencyPercent;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public void setOriginalBudget(int originalBudget) {
        this.originalBudget = originalBudget;
    }

    // Summary method
    public String getSummary() {
        return String.format("Selected %d items for ₹%,d (%.1f%% of budget used)",
                totalItems, totalCost, efficiencyPercent);
    }
}


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

interface SimpleKnapsackService {
    SimpleKnapsackResult findBestProducts(List<Product> products, int budget, int maxItems);

    List<Map<String, Object>> convertProductsToJsonFormat(List<Product> products);
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

    @Override
    public List<Product> getProductsNeedingReorder() {
        return productRepository.findProductsNeedingReorder();
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

    @Override
    public List<Sale> getRecentSales(int days) {
        LocalDate fromDate = LocalDate.now().minusDays(days);
        return saleRepository.findRecentSales(fromDate);
    }

    @Override
    public List<Sale> getSalesByProductAndDateRange(Integer productId, LocalDate startDate, LocalDate endDate) {
        return saleRepository.findByProductAndDateRange(productId, startDate, endDate);
    }

    @Override
    public Long getTotalSalesQuantityForProduct(Integer productId, LocalDate startDate, LocalDate endDate) {
        return saleRepository.sumQuantityByProductAndDateRange(productId, startDate, endDate).orElse(0L);
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

    @Override
    public List<Customer> searchCustomers(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }
}

@Service
class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void createOrder(Order order) {
        if (order.getTotalAmount() == null && order.getUnitPrice() != null && order.getQuantity() != null) {
            order.setTotalAmount(order.getUnitPrice() * order.getQuantity());
        }
        orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAllOrderByOrderDateDesc();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}

@Service
class ForecastServiceImpl implements ForecastService {
    @Autowired
    private ForecastRepository forecastRepository;

    @Autowired
    private SaleService saleService;

    @Autowired
    private ProductService productService;

    @Override
    public void generateForecast(Integer productId) {
        Product product = productService.getProductById(productId);
        if (product == null)
            return;

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(12);

        List<Sale> historicalSales = saleService.getSalesByProductAndDateRange(productId, startDate, endDate);

        for (int i = 1; i <= 3; i++) {
            LocalDate forecastDate = endDate.plusMonths(i);
            String season = getSeason(forecastDate);

            Integer predictedDemand = calculateSeasonalDemand(historicalSales, season, forecastDate);
            Double confidence = calculateConfidence(historicalSales, season);

            Optional<Forecast> existingForecast = forecastRepository.findByProductAndForecastFor(productId,
                    forecastDate.withDayOfMonth(1));

            Forecast forecast;
            if (existingForecast.isPresent()) {
                forecast = existingForecast.get();
                forecast.setPredictedDemand(predictedDemand);
                forecast.setConfidence(confidence);
                forecast.setSeason(season);
            } else {
                forecast = new Forecast(product, season, predictedDemand, confidence, forecastDate.withDayOfMonth(1),
                        "SEASONAL");
            }

            forecastRepository.save(forecast);
        }
    }

    private String getSeason(LocalDate date) {
        Month month = date.getMonth();
        switch (month) {
            case DECEMBER:
            case JANUARY:
            case FEBRUARY:
                return "WINTER";
            case MARCH:
            case APRIL:
            case MAY:
                return "SPRING";
            case JUNE:
            case JULY:
            case AUGUST:
                return "SUMMER";
            case SEPTEMBER:
            case OCTOBER:
            case NOVEMBER:
                return "AUTUMN";
            default:
                return "UNKNOWN";
        }
    }

    private Integer calculateSeasonalDemand(List<Sale> historicalSales, String season, LocalDate forecastDate) {
        if (historicalSales.isEmpty())
            return 5;

        Map<String, List<Sale>> salesBySeason = new HashMap<>();
        for (Sale sale : historicalSales) {
            String saleSeason = getSeason(sale.getDate());
            salesBySeason.computeIfAbsent(saleSeason, k -> new ArrayList<>()).add(sale);
        }

        List<Sale> seasonSales = salesBySeason.get(season);
        if (seasonSales == null || seasonSales.isEmpty()) {
            double avgDemand = historicalSales.stream()
                    .mapToInt(Sale::getQuantity)
                    .average()
                    .orElse(5.0);
            return (int) Math.ceil(avgDemand);
        }

        double seasonalAvg = seasonSales.stream()
                .mapToInt(Sale::getQuantity)
                .average()
                .orElse(5.0);

        double trendFactor = 1.0 + (Math.random() * 0.2 - 0.1);

        return Math.max(1, (int) Math.ceil(seasonalAvg * trendFactor));
    }

    private Double calculateConfidence(List<Sale> historicalSales, String season) {
        if (historicalSales.size() < 3)
            return 0.6;

        double mean = historicalSales.stream().mapToInt(Sale::getQuantity).average().orElse(1.0);
        double variance = historicalSales.stream()
                .mapToDouble(sale -> Math.pow(sale.getQuantity() - mean, 2))
                .average()
                .orElse(1.0);

        double stdDev = Math.sqrt(variance);
        double cv = stdDev / mean;

        double confidence = Math.max(0.5, 1.0 - cv);
        return BigDecimal.valueOf(confidence).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public List<Forecast> getForecastsByProduct(Integer productId) {
        return forecastRepository.findByProductId(productId);
    }

    @Override
    public List<Forecast> getSeasonalForecasts(String season) {
        return forecastRepository.findBySeason(season);
    }

    @Override
    public void generateAllProductForecasts() {
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            generateForecast(product.getId());
        }
    }

    @Override
    public Forecast getForecastForProductAndDate(Integer productId, LocalDate forecastFor) {
        return forecastRepository.findByProductAndForecastFor(productId, forecastFor).orElse(null);
    }
}

    @Service
    class SimpleKnapsackServiceImpl implements SimpleKnapsackService {

        @Override
        public SimpleKnapsackResult findBestProducts(List<Product> products, int budget, int maxItems) {
            List<SimpleKnapsackProduct> availableProducts = products.stream()
                    .filter(product -> product.getPrice() != null && product.getPrice() > 0)
                    .filter(product -> product.getQuantity() != null && product.getQuantity() > 0)
                    .map(SimpleKnapsackProduct::new)
                    .collect(Collectors.toList());

            if (availableProducts.isEmpty()) {
                SimpleKnapsackResult emptyResult = new SimpleKnapsackResult();
                emptyResult.setEmpty(true);
                return emptyResult;
            }

            return runBudgetUtilizationKnapsack(availableProducts, budget, maxItems);
        }

    private SimpleKnapsackResult runBudgetUtilizationKnapsack(List<SimpleKnapsackProduct> products, int budget, int maxItems) {
    int n = products.size();

    if (n == 0 || budget <= 0 || maxItems <= 0) {
        SimpleKnapsackResult emptyResult = new SimpleKnapsackResult();
        emptyResult.setEmpty(true);
        emptyResult.setOriginalBudget(budget);
        return emptyResult;
    }

    // dp[b][k] = max value achievable with budget b and up to k items
    int[][] dp = new int[budget + 1][maxItems + 1];
    boolean[][][] chosen = new boolean[n + 1][budget + 1][maxItems + 1];

    for (int i = 1; i <= n; i++) {
        SimpleKnapsackProduct current = products.get(i - 1);

        for (int b = budget; b >= current.price; b--) {
            for (int k = 1; k <= maxItems; k++) {
                int newValue = dp[b - current.price][k - 1] + current.value;

                if (newValue > dp[b][k]) {
                    dp[b][k] = newValue;
                    chosen[i][b][k] = true;
                }
            }
        }
    }

    // Find the best solution
    int bestValue = 0;
    int bestBudget = 0;
    int bestItems = 0;
    for (int b = 0; b <= budget; b++) {
        for (int k = 0; k <= maxItems; k++) {
            if (dp[b][k] > bestValue) {
                bestValue = dp[b][k];
                bestBudget = b;
                bestItems = k;
            }
        }
    }

    // Backtrack to find selected products
    List<SimpleKnapsackProduct> selectedProducts = new ArrayList<>();
    int b = bestBudget;
    int k = bestItems;

    for (int i = n; i > 0 && k > 0; i--) {
        if (b >= products.get(i - 1).price && chosen[i][b][k]) {
            SimpleKnapsackProduct product = products.get(i - 1);
            product.selected = true;
            selectedProducts.add(product);
            b -= product.price;
            k--;
        }
    }

    return buildResult(selectedProducts, budget);
}



        private SimpleKnapsackResult buildResult(List<SimpleKnapsackProduct> selectedProducts, int originalBudget) {
        SimpleKnapsackResult result = new SimpleKnapsackResult();
        
        int totalCost = selectedProducts.stream().mapToInt(p -> p.price).sum();
        int totalValue = selectedProducts.stream().mapToInt(p -> p.value).sum();
        
        result.setSelectedProducts(selectedProducts);
        result.setTotalCost(totalCost);
        result.setTotalValue(totalValue);
        result.setTotalItems(selectedProducts.size());
        result.setRemainingBudget(originalBudget - totalCost);
        result.setOriginalBudget(originalBudget);
        
        // Calculate efficiency percentage
        double efficiencyPercent = originalBudget > 0 ? ((double) totalCost / originalBudget * 100) : 0.0;
        result.setEfficiencyPercent(efficiencyPercent);
        
        // Mark as empty if no products selected
        result.setEmpty(selectedProducts.isEmpty());
        
        return result;
    }






//debug
public void debugKnapsack(List<SimpleKnapsackProduct> products, int budget, int maxItems) {
    System.out.println("=== KNAPSACK DEBUG ===");
    System.out.println("Budget: " + budget);
    System.out.println("Max Items: " + maxItems);
    System.out.println("Available Products: " + products.size());
    
    // Sort by efficiency ratio for debugging
    products.sort((a, b) -> Double.compare((double)b.value/b.price, (double)a.value/a.price));
    
    System.out.println("Top 10 most efficient products:");
    for (int i = 0; i < Math.min(10, products.size()); i++) {
        SimpleKnapsackProduct p = products.get(i);
        double ratio = (double)p.value / p.price;
        System.out.printf("%d. %s - Price: %d, Value: %d, Ratio: %.2f%n", 
            i+1, p.name, p.price, p.value, ratio);
    }
    
    // Simple greedy selection for comparison
    System.out.println("\nGreedy selection (for comparison):");
    int greedyBudget = budget;
    int greedyItems = 0;
    int greedyCost = 0;
    int greedyValue = 0;
    
    for (SimpleKnapsackProduct p : products) {
        if (greedyBudget >= p.price && greedyItems < maxItems) {
            greedyBudget -= p.price;
            greedyCost += p.price;
            greedyValue += p.value;
            greedyItems++;
            System.out.printf("Selected: %s (Price: %d, Value: %d)%n", p.name, p.price, p.value);
        }
        if (greedyItems >= maxItems) break;
    }
    
    System.out.printf("Greedy Result: %d items, Cost: %d, Value: %d, Remaining: %d%n", 
        greedyItems, greedyCost, greedyValue, greedyBudget);
    System.out.println("=== END DEBUG ===");
}


    @Override
    public List<Map<String, Object>> convertProductsToJsonFormat(List<Product> products) {
        return products.stream().map(product -> {
            SimpleKnapsackProduct kProduct = new SimpleKnapsackProduct(product);
            Map<String, Object> map = new HashMap<>();
            map.put("id", kProduct.id);
            map.put("name", kProduct.name != null ? kProduct.name : "Unknown Product");
            map.put("category", kProduct.category != null ? kProduct.category : "Other");
            map.put("price", kProduct.price);
            map.put("stock", kProduct.stock);
            map.put("value", kProduct.value);
            map.put("ratio",
                    kProduct.price > 0 ? String.format("%.2f", (double) kProduct.value / kProduct.price) : "0.00");
            return map;
        }).collect(Collectors.toList());
    }
}

@Controller
class HomeController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SaleService saleService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SimpleKnapsackService simpleKnapsackService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }

        List<Product> products = productService.getAllProducts();
        long totalProducts = productService.countProducts();
        long lowStockProducts = productService.countLowStockProducts();
        double totalValue = productService.getTotalInventoryValue();

        long pendingOrders = orderService.countOrdersByStatus(OrderStatus.PENDING);
        long processingOrders = orderService.countOrdersByStatus(OrderStatus.PROCESSING);

        List<Product> recentProducts = products.stream()
                .sorted((p1, p2) -> p2.getDate() != null && p1.getDate() != null ? p2.getDate().compareTo(p1.getDate())
                        : 0)
                .limit(5)
                .toList();

        List<Sale> recentSales = saleService.getRecentSales(7);

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("processingOrders", processingOrders);
        model.addAttribute("recentProducts", recentProducts);
        model.addAttribute("recentSales", recentSales.stream().limit(5).toList());

        return "dashboard";
    }

    @GetMapping("/reports")
    public String showReports(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";

        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);

        List<String> productNames = products.stream()
                .map(Product::getName)
                .toList();
        List<Integer> productQuantities = products.stream()
                .map(Product::getQuantity)
                .map(q -> q != null ? q : 0)
                .toList();
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
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/signup")
    public String postSignup(@ModelAttribute User user,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            if (userService.existsByEmail(user.getEmail().toLowerCase())) {
                model.addAttribute("error", "Email already exists. Please use a different email.");
                return "signup";
            }

            user.setEmail(user.getEmail().toLowerCase());

            userService.signUp(user);
            redirectAttributes.addFlashAttribute("success", "Account created successfully! Please login.");

            return "redirect:/";

        } catch (Exception e) {
            model.addAttribute("error", "Error creating account: " + e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/optimizer")
    public String showOptimizer(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }

        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", simpleKnapsackService.convertProductsToJsonFormat(products));
        model.addAttribute("totalProducts", products.size());

        return "optimizer";
    }

    @PostMapping("/optimize")
    @ResponseBody
    public ResponseEntity<SimpleKnapsackResult> optimizeProductSelection(
            @RequestParam int budget,
            @RequestParam(defaultValue = "5") int maxItems,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer minStock,
            HttpSession session) {

        if (session.getAttribute("validuser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<Product> products = productService.getAllProducts();

            // Apply filters
            List<Product> filteredProducts = products.stream()
                    .filter(p -> p.getPrice() != null && p.getQuantity() != null)
                    .filter(p -> category == null || category.isEmpty() ||
                            (p.getCategory() != null && p.getCategory().equals(category)))
                    .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                    .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                    .filter(p -> minStock == null || p.getQuantity() >= minStock)
                    .filter(p -> p.getQuantity() > 0) // Only products in stock
                    .collect(Collectors.toList());

            SimpleKnapsackResult result = simpleKnapsackService.findBestProducts(filteredProducts, budget, maxItems);

            result.setOriginalBudget(budget);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("Error in optimization: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/products")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllProducts(HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<Product> products = productService.getAllProducts();

            if (products == null) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            List<Map<String, Object>> productsJson = products.stream().map(product -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", product.getId());
                map.put("name", product.getName() != null ? product.getName() : "Unknown Product");
                map.put("category", product.getCategory() != null ? product.getCategory() : "Other");
                map.put("price", product.getPrice() != null ? product.getPrice().intValue() : 0);
                map.put("stock", product.getQuantity() != null ? product.getQuantity() : 0);

                // Calculate value using the same logic as SimpleKnapsackProduct
                int price = product.getPrice() != null ? product.getPrice().intValue() : 0;
                int stock = product.getQuantity() != null ? product.getQuantity() : 0;
                int value = calculateProductValue(product, price, stock);

                map.put("value", value);
                map.put("ratio", price > 0 ? String.format("%.2f", (double) value / price) : "0.00");

                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(productsJson);
        } catch (Exception e) {
            System.err.println("Error in getAllProducts API: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatistics(HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

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

                double avgPrice = products.stream()
                        .filter(p -> p.getPrice() != null)
                        .mapToDouble(Product::getPrice)
                        .average()
                        .orElse(0.0);
                stats.put("avgPrice", Math.round(avgPrice));

                double totalValue = products.stream()
                        .mapToDouble(p -> {
                            double price = p.getPrice() != null ? p.getPrice() : 0.0;
                            int quantity = p.getQuantity() != null ? p.getQuantity() : 0;
                            return price * quantity;
                        })
                        .sum();
                stats.put("totalValue", Math.round(totalValue));

                long categoriesCount = products.stream()
                        .map(Product::getCategory)
                        .filter(Objects::nonNull)
                        .distinct()
                        .count();
                stats.put("categoriesCount", categoriesCount);
            }

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Error in getStatistics API: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper method for calculating product value (add this to HomeController)
    private int calculateProductValue(Product product, int price, int stock) {
        int baseValue = price;
        int stockBonus = Math.min(stock * 2, baseValue / 4);
        int categoryBonus = getCategoryBonus(product.getCategory(), baseValue);
        return baseValue + stockBonus + categoryBonus;
    }

    private int getCategoryBonus(String category, int baseValue) {
        if (category == null)
            return 0;

        switch (category.toLowerCase()) {
            case "electronics":
                return baseValue / 10;
            case "premium":
                return baseValue / 8;
            case "bestseller":
                return baseValue / 6;
            case "gadgets":
                return baseValue / 12;
            default:
                return 0;
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

            String uploadDir = "uploads/";
            File uploadPath = new File(uploadDir);

            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            if (!imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);

                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

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

    @GetMapping("/sales")
    public String showSalesForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }

        model.addAttribute("sale", new Sale());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("sales", saleService.getAllSales());
        model.addAttribute("customer", new Customer());

        return "sales";
    }

    @GetMapping("/sales/add")
    public String showAddSaleForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";

        model.addAttribute("sale", new Sale());
        model.addAttribute("products", productService.getAllProducts());
        return "add-sale";
    }

    @PostMapping("/sales/add")
    public String addSale(@ModelAttribute Sale sale,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (session.getAttribute("validuser") == null)
            return "redirect:/";

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
            redirectAttributes.addFlashAttribute("success", "Sale added successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding sale: " + e.getMessage());
        }

        return "redirect:/sales";
    }

    @GetMapping("/sales/{id}/receipt")
    public void generateReceipt(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Sale sale = saleService.getSaleById(id);

        if (sale == null)
            return;

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

    @GetMapping
    public String listCustomers(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";

        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("customer", new Customer());
        return "customers";
    }

    @GetMapping("/add")
    public String showAddCustomerForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";

        model.addAttribute("customer", new Customer());
        return "customers";
    }

    @PostMapping("/add")
    public String saveOrUpdateCustomer(@ModelAttribute Customer customer,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";

        try {
            customerService.saveCustomer(customer);
            redirectAttributes.addFlashAttribute("success",
                    customer.getId() == null ? "Customer added successfully!" : "Customer updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving customer: " + e.getMessage());
        }

        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";

        Customer customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";

        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("success", "Customer deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting customer: " + e.getMessage());
        }

        return "redirect:/customers";
    }
}

@Controller
@RequestMapping("/orders")
class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String listOrders(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";

        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
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
    public String createOrder(@ModelAttribute Order order,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";

        try {
            Customer customer = customerService.getCustomerById(order.getCustomer().getId());
            Product product = productService.getProductById(order.getProduct().getId());

            if (customer == null || product == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid customer or product selected");
                return "redirect:/orders";
            }

            order.setCustomer(customer);
            order.setProduct(product);
            order.setUnitPrice(product.getPrice());
            order.setOrderDate(LocalDate.now());

            orderService.createOrder(order);
            redirectAttributes.addFlashAttribute("success", "Order created successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating order: " + e.getMessage());
        }

        return "redirect:/orders";
    }

    @PostMapping("/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id,
            @RequestParam OrderStatus status,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";

        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Order status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating order status: " + e.getMessage());
        }

        return "redirect:/orders";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";

        try {
            orderService.deleteOrder(id);
            redirectAttributes.addFlashAttribute("success", "Order deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting order: " + e.getMessage());
        }

        return "redirect:/orders";
    }
}

@Controller
@RequestMapping("/forecasts")
class ForecastController {

    @Autowired
    private ForecastService forecastService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showForecasts(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }

        try {
            List<Product> products = productService.getAllProducts();
            Map<Integer, List<Forecast>> productForecasts = new HashMap<>();

            for (Product product : products) {
                try {
                    List<Forecast> forecasts = forecastService.getForecastsByProduct(product.getId());
                    productForecasts.put(product.getId(), forecasts != null ? forecasts : new ArrayList<>());
                } catch (Exception e) {
                    productForecasts.put(product.getId(), new ArrayList<>());
                }
            }

            int totalForecasts = productForecasts.values().stream()
                    .mapToInt(List::size)
                    .sum();

            model.addAttribute("products", products);
            model.addAttribute("productForecasts", productForecasts);
            model.addAttribute("seasons", List.of("SPRING", "SUMMER", "AUTUMN", "WINTER"));
            model.addAttribute("totalProducts", products.size());
            model.addAttribute("totalForecasts", totalForecasts);

        } catch (Exception e) {
            model.addAttribute("error", "Error loading forecasts: " + e.getMessage());
            model.addAttribute("products", new ArrayList<>());
            model.addAttribute("productForecasts", new HashMap<>());
            model.addAttribute("seasons", List.of("SPRING", "SUMMER", "AUTUMN", "WINTER"));
        }

        return "forecasts";
    }

    @PostMapping("/generate/{productId}")
    public String generateForecast(@PathVariable Integer productId,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }

        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Product not found!");
                return "redirect:/forecasts";
            }

            forecastService.generateForecast(productId);

            List<Forecast> forecasts = forecastService.getForecastsByProduct(productId);

            redirectAttributes.addFlashAttribute("success",
                    "Forecast generated successfully for " + product.getName() + "! Generated " + forecasts.size()
                            + " forecast periods.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error generating forecast: " + e.getMessage());
        }

        return "redirect:/forecasts";
    }

    @PostMapping("/generate-all")
    public String generateAllForecasts(RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("validuser") == null) {
            return "redirect:/";
        }

        try {
            List<Product> products = productService.getAllProducts();

            if (products == null || products.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No products found to generate forecasts for!");
                return "redirect:/forecasts";
            }

            int successCount = 0;
            int errorCount = 0;

            for (Product product : products) {
                try {
                    forecastService.generateForecast(product.getId());
                    successCount++;
                } catch (Exception e) {
                    errorCount++;
                }
            }

            if (successCount > 0) {
                redirectAttributes.addFlashAttribute("success",
                        "Forecasts generated! Success: " + successCount + " products" +
                                (errorCount > 0 ? ", Errors: " + errorCount + " products" : ""));
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "No forecasts could be generated. Check if products have sufficient historical sales data.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error generating forecasts: " + e.getMessage());
        }

        return "redirect:/forecasts";
    }
}

@Component
class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User adminUser = new User();
            adminUser.setUname("admin");
            adminUser.setEmail("admin@kapiltraders.com");
            adminUser.setPassword("admin123");
            userRepository.save(adminUser);

            System.out.println("Created default admin user:");
            System.out.println("Email: admin@kapiltraders.com");
            System.out.println("Password: admin123");
        }
    }
}
