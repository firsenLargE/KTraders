package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors; // <-- added

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@SpringBootApplication
public class KapilTradersApplication {

    public static void main(String[] args) {
        SpringApplication.run(KapilTradersApplication.class, args);
    }
}

/*
 * ============================
 * ========== ENTITIES =========
 * ============================
 */

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
    private Integer maxStockLevel;
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

/*
 * =============================
 * ========= REPOSITORIES =======
 * =============================
 */

@Repository
interface UserRepository extends org.springframework.data.jpa.repository.JpaRepository<User, Integer> {
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

/*
 * =============================
 * ======== SERVICE APIs ========
 * =============================
 */

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

/*
 * =====================================
 * ========= SERVICE IMPLEMENTATIONS ====
 * =====================================
 */

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
        return productRepository.findAll().stream()
                .mapToDouble(p -> (p.getPrice() != null ? p.getPrice() : 0.0)
                        * (p.getQuantity() != null ? p.getQuantity() : 0))
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

    /*
     * =========================
     * ========== API ==========
     * =========================
     */

    @Override
    public void generateForecast(Integer productId) {
        Product product = productService.getProductById(productId);
        if (product == null)
            return;

        // last 12 months window
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(12);

        List<Sale> historicalSales = saleService.getSalesByProductAndDateRange(productId, startDate, endDate);

        // Build monthly time series and fit a simple Trend + Seasonality model
        List<Monthly> series = toMonthlySeries(
                historicalSales,
                startDate.withDayOfMonth(1),
                endDate.withDayOfMonth(1));

        Decomp d = fitTrendSeason(series);

        // Forecast the next 3 months (month t = lastT + 1..3)
        java.time.YearMonth lastYM = java.time.YearMonth.from(endDate.withDayOfMonth(1));
        int lastT = series.isEmpty() ? 0 : series.get(series.size() - 1).t;

        for (int i = 1; i <= 3; i++) {
            java.time.YearMonth ym = lastYM.plusMonths(i);
            int tFuture = lastT + i;
            int monthIdx = ym.getMonthValue() - 1;

            Integer predictedDemand = predictMonth(d, tFuture, monthIdx);
            Double confidence = confidenceFromMape(d.mape);

            String season = getSeason(ym.atDay(1));
            LocalDate forecastFor = ym.atDay(1);

            Optional<Forecast> existing = forecastRepository.findByProductAndForecastFor(productId, forecastFor);

            Forecast f = existing.orElseGet(
                    () -> new Forecast(product, season, predictedDemand, confidence, forecastFor, "TREND_SEASONAL"));

            f.setPredictedDemand(predictedDemand);
            f.setConfidence(confidence);
            f.setSeason(season);
            f.setMethod("TREND_SEASONAL");
            // Store “accuracy” as (1 - MAPE) in [0..1]
            f.setAccuracy(BigDecimal.valueOf(1.0 - Math.min(1.0, d.mape))
                    .setScale(2, RoundingMode.HALF_UP).doubleValue());

            forecastRepository.save(f);
        }
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
        productService.getAllProducts().forEach(p -> generateForecast(p.getId()));
    }

    @Override
    public Forecast getForecastForProductAndDate(Integer productId, LocalDate forecastFor) {
        return forecastRepository.findByProductAndForecastFor(productId, forecastFor).orElse(null);
    }

    /*
     * ==========================================
     * ======= Helpers: season/trend/metrics =====
     * ==========================================
     */

    private String getSeason(LocalDate date) {
        switch (date.getMonth()) {
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

    /**
     * A monthly observation: t=1..N (time index), qty (sum in that month),
     * monthIndex=0..11
     */
    private static final class Monthly {
        final int t;
        final int qty;
        final int monthIndex;

        Monthly(int t, int qty, int monthIndex) {
            this.t = t;
            this.qty = qty;
            this.monthIndex = monthIndex;
        }
    }

    private static final int MIN_TRAIN_MONTHS = 6;

    /**
     * Aggregate raw sales into a contiguous monthly time series between
     * [startYM..endYM].
     */
    private List<Monthly> toMonthlySeries(List<Sale> sales, LocalDate startFirst, LocalDate endFirst) {
        java.time.YearMonth ymStart = java.time.YearMonth.from(startFirst);
        java.time.YearMonth ymEnd = java.time.YearMonth.from(endFirst);
        int len = (int) java.time.temporal.ChronoUnit.MONTHS.between(ymStart, ymEnd) + 1;

        Map<java.time.YearMonth, Integer> totals = new HashMap<>();
        for (Sale s : sales) {
            if (s.getDate() == null || s.getQuantity() == null)
                continue;
            java.time.YearMonth ym = java.time.YearMonth.from(s.getDate().withDayOfMonth(1));
            if (ym.isBefore(ymStart) || ym.isAfter(ymEnd))
                continue;
            totals.merge(ym, s.getQuantity(), Integer::sum);
        }

        List<Monthly> out = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            java.time.YearMonth ym = ymStart.plusMonths(i);
            int qty = totals.getOrDefault(ym, 0);
            int monthIdx = ym.getMonthValue() - 1; // 0..11
            out.add(new Monthly(i + 1, qty, monthIdx));
        }
        return out;
    }

    /**
     * Decomposition container: linear trend (a + b*t), multiplicative seasonal
     * indices, MAPE.
     */
    private static final class Decomp {
        double intercept; // a
        double slope; // b
        double[] season = new double[12]; // multiplicative indices per month (mean=1)
        double mape; // backtest error (0..1)
    }

    /**
     * Fit a simple linear regression for trend, then multiplicative seasonal
     * indices.
     */
    private Decomp fitTrendSeason(List<Monthly> series) {
        Decomp d = new Decomp();
        int n = series.size();

        if (n < MIN_TRAIN_MONTHS) {
            // Not enough data: flat mean, no seasonality
            double mean = series.stream().mapToInt(m -> m.qty).average().orElse(5.0);
            d.intercept = mean;
            d.slope = 0.0;
            java.util.Arrays.fill(d.season, 1.0);
            d.mape = 0.40; // low confidence
            return d;
        }

        // 1) OLS trend: qty ~ a + b*t
        double sumT = 0, sumY = 0, sumTT = 0, sumTY = 0;
        for (Monthly m : series) {
            sumT += m.t;
            sumY += m.qty;
            sumTT += m.t * m.t;
            sumTY += m.t * m.qty;
        }
        double b = (n * sumTY - sumT * sumY) / Math.max(1e-9, (n * sumTT - sumT * sumT));
        double a = (sumY - b * sumT) / n;

        // 2) multiplicative seasonal indices
        double[] monthSum = new double[12];
        int[] monthCnt = new int[12];
        for (Monthly m : series) {
            double trend = a + b * m.t;
            double ratio = (trend <= 1e-9 ? 1.0 : m.qty / Math.max(1.0, trend));
            monthSum[m.monthIndex] += ratio;
            monthCnt[m.monthIndex] += 1;
        }
        double sumIdx = 0;
        for (int k = 0; k < 12; k++) {
            d.season[k] = (monthCnt[k] == 0) ? 1.0 : monthSum[k] / monthCnt[k];
            sumIdx += d.season[k];
        }
        // normalize indices so their mean is ~1.0
        double meanIdx = sumIdx / 12.0;
        if (meanIdx != 0)
            for (int k = 0; k < 12; k++)
                d.season[k] /= meanIdx;

        d.intercept = a;
        d.slope = b;

        // 3) backtest MAPE on training
        double apeSum = 0;
        int apeN = 0;
        for (Monthly m : series) {
            double base = a + b * m.t;
            double pred = Math.max(0.0, base) * d.season[m.monthIndex];
            double y = m.qty;
            if (y > 0) {
                apeSum += Math.abs(y - pred) / y;
                apeN++;
            }
        }
        d.mape = (apeN == 0 ? 0.50 : apeSum / apeN);
        return d;
    }

    /** Predict one future month t with seasonal index. */
    private int predictMonth(Decomp d, int tFuture, int monthIndex) {
        double base = d.intercept + d.slope * tFuture;
        double pred = Math.max(0.0, base) * d.season[monthIndex];
        return Math.max(1, (int) Math.round(pred));
    }

    /** Convert error (MAPE) into an intuitive confidence [0.50 .. 0.95]. */
    private double confidenceFromMape(double mape) {
        double c = 1.0 - Math.max(0.0, Math.min(1.0, mape)); // 1 - error
        return BigDecimal.valueOf(0.5 + 0.45 * c).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

/*
 * =====================================
 * ============= CONTROLLERS ============
 * =====================================
 */

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

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";

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
    public String showReports(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("productNames", products.stream().map(Product::getName).toList());
        model.addAttribute("productQuantities",
                products.stream().map(p -> p.getQuantity() != null ? p.getQuantity() : 0).toList());
        model.addAttribute("categoryCounts",
                products.stream().collect(Collectors.groupingBy(Product::getCategory, Collectors.counting())));
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
    public ResponseEntity<List<Map<String, Object>>> getAllProducts(HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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

    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatistics(HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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

    // helper used by /api/products
    private int calculateProductValue(Product p, int price, int stock) {
        double stockFactor = Math.min(Math.max(stock, 0), 100) / 100.0;
        double catFactor = 0.0;
        String cat = (p.getCategory() != null) ? p.getCategory().toLowerCase() : "";
        switch (cat) {
            case "electronics":
                catFactor = 0.12;
                break;
            case "premium":
                catFactor = 0.10;
                break;
            case "bestseller":
                catFactor = 0.08;
                break;
            case "gadgets":
                catFactor = 0.05;
                break;
            default:
                catFactor = 0.00;
        }
        double v = price * (1.0 + catFactor + 0.20 * stockFactor);
        return Math.max(1, (int) Math.round(v));
    }
}

@Controller
class ProductController {
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

    @GetMapping("/add-product")
    public String showAddProductForm(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        model.addAttribute("product", new Product());
        return "add-product";
    }

    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute Product product,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        try {
            if (product.getDate() == null)
                product.setDate(LocalDate.now());
            String uploadDir = "uploads/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists())
                uploadPath.mkdirs();
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
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        Product product = productService.getProductById(id);
        if (product == null)
            return "redirect:/products";
        model.addAttribute("product", product);
        return "edit-product";
    }

    @PostMapping("/edit-product/{id}")
    public String updateProduct(@PathVariable Integer id, @ModelAttribute Product product,
            RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
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
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
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
        double totalValue = products.stream().mapToDouble(
                p -> (p.getPrice() != null ? p.getPrice() : 0.0) * (p.getQuantity() != null ? p.getQuantity() : 0))
                .sum();
        model.addAttribute("totalValue", totalValue);
    }
}

@Controller
@RequestMapping("/optimizer")
class OptimizerController {

    @Autowired
    private BudgetPlannerService budgetPlannerService;

    @GetMapping
    public String showOptimizer(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
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
    public String ping() {
        return "ok";
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
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
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
    public String addSale(@ModelAttribute Sale sale, RedirectAttributes redirectAttributes, HttpSession session) {
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
        response.setHeader("Content-Disposition", "attachment; filename=invoice_" + id + ".pdf");

        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        // === Company Header ===
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        Paragraph store = new Paragraph("KAPIL TRADERS", headerFont);
        store.setAlignment(Element.ALIGN_CENTER);
        doc.add(store);

        Paragraph subHeader = new Paragraph("Address: Kathmandu, Nepal | PAN/VAT: 123456789", normalFont);
        subHeader.setAlignment(Element.ALIGN_CENTER);
        doc.add(subHeader);

        doc.add(new Paragraph(" "));

        // === Buyer Info ===
        PdfPTable buyerTable = new PdfPTable(2);
        buyerTable.setWidthPercentage(100);
        buyerTable.addCell(new Phrase("Buyer Name: " +
                (sale.getCustomer() != null ? sale.getCustomer().getName() : "Walk-in"), normalFont));
        buyerTable.addCell(new Phrase("Invoice No: " + sale.getId(), normalFont));
        buyerTable.addCell(new Phrase("Buyer PAN: " +
                (sale.getCustomer() != null ? sale.getCustomer().getContact() : "-"), normalFont));
        buyerTable.addCell(new Phrase("Date: " + sale.getDate(), normalFont));
        doc.add(buyerTable);

        doc.add(new Paragraph(" "));

        // === Items Table ===
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 3, 1, 1, 2, 2, 2 });
        table.addCell(new Phrase("Item", boldFont));
        table.addCell(new Phrase("Qty", boldFont));
        table.addCell(new Phrase("Rate", boldFont));
        table.addCell(new Phrase("Taxable", boldFont));
        table.addCell(new Phrase("VAT (13%)", boldFont));
        table.addCell(new Phrase("Total", boldFont));

        double totalTaxable = 0;
        double totalVat = 0;
        double totalGross = 0;

        double unitPrice = (sale.getProduct() != null && sale.getProduct().getPrice() != null)
                ? sale.getProduct().getPrice()
                : 0.0;
        int qty = (sale.getQuantity() != null) ? sale.getQuantity() : 0;
        double taxable = unitPrice * qty;
        double vat = taxable * 0.13;
        double gross = taxable + vat;

        table.addCell(sale.getProduct() != null ? sale.getProduct().getName() : "-");
        table.addCell(String.valueOf(qty));
        table.addCell(String.format("%.2f", unitPrice));
        table.addCell(String.format("%.2f", taxable));
        table.addCell(String.format("%.2f", vat));
        table.addCell(String.format("%.2f", gross));

        totalTaxable += taxable;
        totalVat += vat;
        totalGross += gross;

        // Totals row
        PdfPCell empty = new PdfPCell(new Phrase(""));
        empty.setColspan(3);
        table.addCell(empty);
        table.addCell(String.format("%.2f", totalTaxable));
        table.addCell(String.format("%.2f", totalVat));
        table.addCell(String.format("%.2f", totalGross));

        doc.add(table);

        doc.add(new Paragraph(" "));

        // Amount in words
        doc.add(new Paragraph("Amount in words: " + NumberToWordsConverter.convert((long) totalGross) + " only.",
                normalFont));

        doc.add(new Paragraph(" "));

        // Footer
        Paragraph footer = new Paragraph("Authorized Signature", boldFont);
        footer.setAlignment(Element.ALIGN_RIGHT);
        doc.add(footer);

        doc.close();
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
    public String showAddCustomer(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("customer", new Customer());
        return "customers"; // same template with the form on it
    }

    @PostMapping("/add")
    public String saveOrUpdateCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        try {
            customerService.saveCustomer(customer);
            redirectAttributes.addFlashAttribute("success",
                    customer.getId() == null ? "Customer added!" : "Customer updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
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
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
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
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
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
    public String updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status,
            RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
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
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
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
    @Autowired
    private ForecastService forecastService;
    @Autowired
    private ProductService productService;

    @GetMapping
    public String showForecasts(Model model, HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
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
    public String generateForecast(@PathVariable Integer productId, RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
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
        if (session.getAttribute("validuser") == null)
            return "redirect:/";
        try {
            List<Product> products = productService.getAllProducts();
            if (products.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No products found!");
                return "redirect:/forecasts";
            }
            int successCount = 0, errorCount = 0;
            for (Product product : products) {
                try {
                    forecastService.generateForecast(product.getId());
                    successCount++;
                } catch (Exception e) {
                    errorCount++;
                }
            }
            if (successCount > 0)
                redirectAttributes.addFlashAttribute("success",
                        "Forecasts generated for " + successCount + " products, errors: " + errorCount);
            else
                redirectAttributes.addFlashAttribute("error", "No forecasts generated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/forecasts";
    }
}

/*
 * =============================
 * ===== Feature 1: Cash Ledger
 * =============================
 */
@Entity
class CashTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Type type; // IN = sale, OUT = purchase
    private BigDecimal amount;
    private String reference;
    private String counterparty;
    private String notes;
    private LocalDateTime occurredAt;

    public enum Type {
        IN, OUT
    }

    // Getters/Setters...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }
}

interface CashTransactionRepository extends JpaRepository<CashTransaction, Long> {
    List<CashTransaction> findByOccurredAtBetween(LocalDateTime start, LocalDateTime end);
}

@Service
class CashLedgerService {
    private final CashTransactionRepository repo;

    @Autowired
    CashLedgerService(CashTransactionRepository repo) {
        this.repo = repo;
    }

    List<CashTransaction> range(LocalDate from, LocalDate to) {
        return repo.findByOccurredAtBetween(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
    }

    CashTransaction record(CashTransaction tx) {
        return repo.save(tx);
    }
}

@Controller
@RequestMapping("/reports/cash")
class CashLedgerController {
    private final CashLedgerService ledger;

    @Autowired
    CashLedgerController(CashLedgerService ledger) {
        this.ledger = ledger;
    }

    @GetMapping
    public String view(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {
        LocalDate start = from != null ? from : LocalDate.now();
        LocalDate end = to != null ? to : start;
        model.addAttribute("rows", ledger.range(start, end));
        model.addAttribute("from", start);
        model.addAttribute("to", end);
        return "cash_ledger";
    }
}

/*
 * =============================
 * ===== Feature 2: Stock Movement
 * =============================
 */
@Entity
class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Product product;
    @Enumerated(EnumType.STRING)
    private Direction direction;
    private BigDecimal quantity;
    private String reference;
    private String reason;
    private LocalDateTime occurredAt;

    public enum Direction {
        IN, OUT
    }

    // Getters/Setters...
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

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }
}

interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductIdOrderByOccurredAtDesc(Integer productId);
}

@Service
class StockMovementService {
    private final StockMovementRepository repo;

    @Autowired
    StockMovementService(StockMovementRepository repo) {
        this.repo = repo;
    }

    List<StockMovement> byProduct(Integer pid) {
        return repo.findByProductIdOrderByOccurredAtDesc(pid);
    }

    StockMovement record(StockMovement m) {
        return repo.save(m);
    }
}

@Controller
@RequestMapping("/inventory/movements")
class StockMovementController {
    private final StockMovementService svc;

    @Autowired
    StockMovementController(StockMovementService svc) {
        this.svc = svc;
    }

    @GetMapping("/{productId}")
    public String view(@PathVariable Integer productId, Model model) {
        model.addAttribute("rows", svc.byProduct(productId));
        return "stock_in_out";
    }
}

/*
 * =============================
 * ===== Feature 3: Sales Reports
 * =============================
 */

interface ExtendedSaleRepository extends JpaRepository<Sale, Integer> {
    @Query("""
              select new com.example.demo.ProductSalesRow(
                 s.product.id,
                 s.product.name,
                 SUM(COALESCE(s.quantity, 0)),
                 SUM(COALESCE(s.totalPrice, 0.0)),
                 SUM(COALESCE(s.totalPrice, 0.0)) * 0.13,
                 SUM(COALESCE(s.totalPrice, 0.0)) * 1.13
              )
              from Sale s
              where s.date >= :start and s.date < :endExclusive
              group by s.product.id, s.product.name
              order by s.product.name
            """)
    List<ProductSalesRow> aggregateByProduct(
            @Param("start") LocalDate start,
            @Param("endExclusive") LocalDate endExclusive);
}

@Service
class ReportingService {
    private final ExtendedSaleRepository repo;

    ReportingService(ExtendedSaleRepository repo) {
        this.repo = repo;
    }

    List<ProductSalesRow> daily(LocalDate date) {
        return repo.aggregateByProduct(date, date.plusDays(1));
    }

    List<ProductSalesRow> monthly(YearMonth ym) {
        LocalDate start = ym.atDay(1);
        LocalDate endExclusive = ym.plusMonths(1).atDay(1);
        return repo.aggregateByProduct(start, endExclusive);
    }
}

@Controller
@RequestMapping("/reports/sales")
class SalesReportsController {
    private final ReportingService svc;

    @Autowired
    SalesReportsController(ReportingService svc) {
        this.svc = svc;
    }

    @GetMapping("/daily")
    public String daily(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        LocalDate d = date != null ? date : LocalDate.now();
        model.addAttribute("rows", svc.daily(d));
        model.addAttribute("date", d);
        return "daily_sales";
    }

    @GetMapping("/monthly")
public String monthly(@RequestParam(required = false) Integer year,
                      @RequestParam(required = false) Integer month,
                      Model model) {
    YearMonth ym = (year != null && month != null)
            ? YearMonth.of(year, month)
            : YearMonth.now();
    model.addAttribute("rows", svc.monthly(ym));
    model.addAttribute("ym", ym);
    return "monthly_sales";
}

@GetMapping("/daily.pdf")
public void dailyPdf(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        HttpServletResponse response) throws Exception {

    LocalDate d = (date != null) ? date : LocalDate.now();
    List<ProductSalesRow> rows = svc.daily(d);
    exportSalesPdf(
            response,
            "Daily Sales - " + d,
            "Date: " + d,
            rows
    );
}

@GetMapping("/monthly.pdf")
public void monthlyPdf(@RequestParam int year, @RequestParam int month,
                       HttpServletResponse response) throws Exception {
    java.time.YearMonth ym = java.time.YearMonth.of(year, month);
    List<ProductSalesRow> rows = svc.monthly(ym);
    exportSalesPdf(
            response,
            "Monthly Sales - " + ym,
            "Period: " + ym,
            rows
    );
}

/** Reusable PDF builder (no BigDecimal anywhere) */
private void exportSalesPdf(HttpServletResponse response,
                            String title, String subtitle,
                            List<ProductSalesRow> rows) throws Exception {

    response.reset();
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=" +
            title.toLowerCase().replace(' ', '_') + ".pdf");

    com.itextpdf.text.Document doc = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4, 36, 36, 36, 36);
    com.itextpdf.text.pdf.PdfWriter.getInstance(doc, response.getOutputStream());
    doc.open();

    com.itextpdf.text.Font h1 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
    com.itextpdf.text.Font h2 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11);
    com.itextpdf.text.Font th = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD);
    com.itextpdf.text.Font td = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);

    // Header
    com.itextpdf.text.Paragraph pTitle = new com.itextpdf.text.Paragraph(title, h1);
    pTitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
    doc.add(pTitle);
    com.itextpdf.text.Paragraph pSub = new com.itextpdf.text.Paragraph(subtitle, h2);
    pSub.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
    doc.add(pSub);
    doc.add(new com.itextpdf.text.Paragraph(" "));

    // Table
    com.itextpdf.text.pdf.PdfPTable t = new com.itextpdf.text.pdf.PdfPTable(6);
    t.setWidthPercentage(100);
    t.setWidths(new float[]{3f, 1f, 2f, 2f, 2f, 2f});

    addHeaderCell(t, "Product", th);
    addHeaderCell(t, "Qty", th);
    addHeaderCell(t, "Net", th);
    addHeaderCell(t, "VAT (13%)", th);
    addHeaderCell(t, "Gross", th);
    addHeaderCell(t, "Avg Price", th);

    long totalQty = 0L;
    double totalNet = 0.0;
    double totalVat = 0.0;
    double totalGross = 0.0;

    for (ProductSalesRow r : rows) {
        long q = (r.getQty() != null) ? r.getQty() : 0L;
        double net = (r.getNet() != null) ? r.getNet() : 0.0;
        double vat = (r.getVat() != null) ? r.getVat() : 0.0;
        double gross = (r.getGross() != null) ? r.getGross() : 0.0;
        double avgPrice = (q > 0) ? (net / q) : 0.0;

        addCell(t, r.getProductName(), td);
        addCell(t, String.valueOf(q), td);
        addCell(t, money(net), td);
        addCell(t, money(vat), td);
        addCell(t, money(gross), td);
        addCell(t, money(avgPrice), td);

        totalQty += q;
        totalNet += net;
        totalVat += vat;
        totalGross += gross;
    }

    // Totals row
    com.itextpdf.text.pdf.PdfPCell totLabel = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("TOTAL", th));
    totLabel.setColspan(1);
    t.addCell(totLabel);

    addCell(t, String.valueOf(totalQty), th);
    addCell(t, money(totalNet), th);
    addCell(t, money(totalVat), th);
    addCell(t, money(totalGross), th);

    double avgOverall = (totalQty > 0) ? (totalNet / totalQty) : 0.0;
    addCell(t, money(avgOverall), th);

    doc.add(t);
    doc.close();
}

private static void addHeaderCell(com.itextpdf.text.pdf.PdfPTable t, String text, com.itextpdf.text.Font font) {
    com.itextpdf.text.pdf.PdfPCell c = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, font));
    c.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
    t.addCell(c);
}
private static void addCell(com.itextpdf.text.pdf.PdfPTable t, String text, com.itextpdf.text.Font font) {
    com.itextpdf.text.pdf.PdfPCell c = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, font));
    c.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
    t.addCell(c);
}
private static String money(double v) {
    return String.format("%.2f", v);
}


}

/*
 * =============================
 * ===== Feature 4: Breakage/Leakage
 * =============================
 */
@Entity
class BreakageLeakage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Product product;
    private BigDecimal quantity;
    private String reason;
    private LocalDateTime occurredAt;
    private String notes;

    // Getters/Setters...
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

interface BreakageLeakageRepository extends JpaRepository<BreakageLeakage, Long> {
}

@Service
class BreakageLeakageService {
    private final BreakageLeakageRepository repo;
    private final StockMovementService movementSvc;

    @Autowired
    BreakageLeakageService(BreakageLeakageRepository repo, StockMovementService movementSvc) {
        this.repo = repo;
        this.movementSvc = movementSvc;
    }

    BreakageLeakage record(BreakageLeakage b) {
        BreakageLeakage saved = repo.save(b);
        StockMovement m = new StockMovement();
        m.setProduct(b.getProduct());
        m.setQuantity(b.getQuantity());
        m.setDirection(StockMovement.Direction.OUT);
        m.setReason("breakage/leakage: " + b.getReason());
        m.setOccurredAt(b.getOccurredAt());
        movementSvc.record(m);
        return saved;
    }

    List<BreakageLeakage> all() {
        return repo.findAll();
    }
}

@Controller
@RequestMapping("/inventory/breakage")
class BreakageLeakageController {
    private final BreakageLeakageService svc;

    @Autowired
    BreakageLeakageController(BreakageLeakageService svc) {
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

/*
 * =====================================
 * ======== DATA INITIALIZER ============
 * =====================================
 */

@Component
class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User adminUser = new User("admin", "admin@kapiltraders.com", "admin123");
            userRepository.save(adminUser);
            System.out.println("Created default admin user: admin@kapiltraders.com / admin123");
        }
    }
}

/*
 * =====================================
 * ======== Algorithm part ============
 * =====================================
 */

enum ReplenishmentStrategy {
    ROUND_ROBIN, // spread spend: 1 unit per item in priority order, loop
    LOW_STOCK_FIRST, // strictly fill biggest gap to min first, then toward max
    BEST_VALUE // (gap + category weight) per rupee, greedy to max
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer requestedBudget;
    private Integer totalCost;
    private Integer totalUnits;
    private String strategy; // store enum name
    private LocalDate createdAt = LocalDate.now();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplenishmentLine> lines = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Integer getRequestedBudget() {
        return requestedBudget;
    }

    public void setRequestedBudget(Integer requestedBudget) {
        this.requestedBudget = requestedBudget;
    }

    public Integer getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }

    public Integer getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Integer totalUnits) {
        this.totalUnits = totalUnits;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public List<ReplenishmentLine> getLines() {
        return lines;
    }

    public void setLines(List<ReplenishmentLine> lines) {
        this.lines = lines;
    }
}

@Entity
@Table(name = "replenishment_lines")
class ReplenishmentLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private ReplenishmentPlan plan;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer units;
    private Integer unitPrice;
    private Integer lineCost;

    public Long getId() {
        return id;
    }

    public ReplenishmentPlan getPlan() {
        return plan;
    }

    public void setPlan(ReplenishmentPlan plan) {
        this.plan = plan;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getLineCost() {
        return lineCost;
    }

    public void setLineCost(Integer lineCost) {
        this.lineCost = lineCost;
    }
}

@Repository
interface ReplenishmentPlanRepository
        extends org.springframework.data.jpa.repository.JpaRepository<ReplenishmentPlan, Long> {
}

@Repository
interface ReplenishmentLineRepository
        extends org.springframework.data.jpa.repository.JpaRepository<ReplenishmentLine, Long> {
}

interface BudgetPlannerService {
    PurchasePlanDTO planPurchases(int budget,
            ReplenishmentStrategy strategy,
            boolean persistPlan,
            java.util.Set<Integer> includeProductIds);
}

@Service
class BudgetPlannerServiceImpl implements BudgetPlannerService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired(required = false)
    private ReplenishmentPlanRepository planRepo;

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
        if (budget <= 0)
            return out;

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
        if (eligible.isEmpty())
            return out;

        // 2) Build scored view
        List<Cand> scored = new ArrayList<>();
        for (Product p : eligible) {
            Cand c = new Cand();
            c.p = p;
            c.price = p.getPrice() == null ? 0 : p.getPrice().intValue();
            c.qty = Optional.ofNullable(p.getQuantity()).orElse(0);
            c.minLvl = Optional.ofNullable(p.getMinStockLevel()).orElse(0);
            c.maxLvl = Optional.ofNullable(p.getMaxStockLevel()).orElse(Integer.MAX_VALUE);

            c.gapToMin = Math.max(0, c.minLvl - c.qty);
            String cat = (p.getCategory() == null ? "" : p.getCategory().toLowerCase());
            c.catW = switch (cat) {
                case "electronics" -> 0.12;
                case "premium" -> 0.10;
                case "bestseller" -> 0.08;
                case "gadgets" -> 0.05;
                default -> 0.00;
            };
            scored.add(c);
        }

        // 3) Run chosen strategy
        Map<Integer, Integer> bought = switch (strategy) {
            case ROUND_ROBIN -> purchaseRoundRobin(scored, budget);
            case LOW_STOCK_FIRST -> purchaseLowStockFirst(scored, budget);
            case BEST_VALUE -> purchaseBestValue(scored, budget);
            // If you still use the old enum names, map them like:
            // case MAX_UNITS -> purchaseBestValue(scored, budget); // cheapest/value
            // case AVOID_STOCKOUTS -> purchaseLowStockFirst(scored, budget);
            // case HYBRID -> purchaseRoundRobin(scored, budget);
        };

        // 4) Build DTO
        for (var e : bought.entrySet()) {
            Integer pid = e.getKey();
            int units = e.getValue();
            if (units <= 0)
                continue;
            Product p = all.stream().filter(pp -> Objects.equals(pp.getId(), pid)).findFirst().orElse(null);
            if (p == null)
                continue;
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
                Product p = all.stream().filter(pp -> Objects.equals(pp.getId(), dto.productId)).findFirst()
                        .orElse(null);
                if (p == null)
                    continue;
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
    private Map<Integer, Integer> purchaseRoundRobin(List<Cand> scored, int budget) {
        // priority = (gap + 100*catW + 1) / price
        scored.forEach(c -> c.priority = (c.gapToMin + 100 * c.catW + 1.0) / Math.max(1, c.price));
        scored.sort((a, b) -> {
            int cmp = Double.compare(b.priority, a.priority);
            return (cmp != 0) ? cmp : Integer.compare(a.price, b.price);
        });

        Map<Integer, Integer> bought = new HashMap<>();
        int remaining = budget;
        boolean purchased;
        do {
            purchased = false;
            for (Cand c : scored) {
                int have = bought.getOrDefault(c.p.getId(), 0);
                int currentQty = c.qty + have;
                if (currentQty >= c.maxLvl)
                    continue;
                if (c.price > remaining)
                    continue;
                remaining -= c.price;
                bought.merge(c.p.getId(), 1, Integer::sum);
                purchased = true;
            }
        } while (purchased);
        return bought;
    }

    // 2) LOW_STOCK_FIRST: fill to min level by largest gap, then toward max
    private Map<Integer, Integer> purchaseLowStockFirst(List<Cand> scored, int budget) {
        Map<Integer, Integer> bought = new HashMap<>();
        int remaining = budget;

        List<Cand> needingMin = scored.stream()
                .filter(c -> c.gapToMin > 0)
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.gapToMin, a.gapToMin);
                    return (cmp != 0) ? cmp : Integer.compare(a.price, b.price);
                })
                .toList();

        // Phase A: reach min
        for (Cand c : needingMin) {
            while (remaining >= c.price) {
                int have = bought.getOrDefault(c.p.getId(), 0);
                int currentQty = c.qty + have;
                if (currentQty >= c.minLvl)
                    break;
                if (currentQty >= c.maxLvl)
                    break;
                remaining -= c.price;
                bought.merge(c.p.getId(), 1, Integer::sum);
            }
            if (remaining < 1)
                break;
        }
        if (remaining < 1)
            return bought;

        // Phase B: continue to max in same order
        for (Cand c : needingMin) {
            while (remaining >= c.price) {
                int have = bought.getOrDefault(c.p.getId(), 0);
                int currentQty = c.qty + have;
                if (currentQty >= c.maxLvl)
                    break;
                remaining -= c.price;
                bought.merge(c.p.getId(), 1, Integer::sum);
            }
            if (remaining < 1)
                break;
        }
        return bought;
    }

    // 3) BEST_VALUE (Top-3, ROUND-ROBIN):
    // Pick the three highest-priority active items, then buy 1 unit per pass across
    // them
    // until budget runs out or they hit caps. Spreads spend so multiple lines
    // appear.
    private Map<Integer, Integer> purchaseBestValue(List<Cand> scored, int budget) {
        Map<Integer, Integer> bought = new HashMap<>();
        int remaining = budget;
        if (remaining <= 0 || scored == null || scored.isEmpty())
            return bought;

        // priority = (gapToMin + 100*catW + 1) / max(1, price)
        scored.forEach(c -> c.priority = (c.gapToMin + 100 * c.catW + 1.0) / Math.max(1, c.price));

        // Active = not already at/over max; take Top-3 by priority desc, then cheaper
        // first
        List<Cand> top3 = scored.stream()
                .filter(c -> c.qty < c.maxLvl)
                .sorted((a, b) -> {
                    int cmp = Double.compare(b.priority, a.priority);
                    return (cmp != 0) ? cmp : Integer.compare(a.price, b.price);
                })
                .limit(3)
                .toList();
        System.out.println("Top-3 BEST_VALUE: " + top3.stream()
                .map(c -> c.p.getId() + ":" + c.price + " pr=" + String.format("%.4f", c.priority))
                .toList());

        if (top3.isEmpty())
            return bought;

        // Round-robin passes across the Top-3
        while (remaining > 0) {
            boolean purchasedThisPass = false;

            // Cheapest *active* price this pass (for early exit)
            int minTopPrice = top3.stream()
                    .filter(c -> (c.qty + bought.getOrDefault(c.p.getId(), 0)) < c.maxLvl)
                    .mapToInt(c -> c.price)
                    .min()
                    .orElse(Integer.MAX_VALUE);

            if (remaining < minTopPrice)
                break;

            for (Cand c : top3) {
                int have = bought.getOrDefault(c.p.getId(), 0);
                int currentQty = c.qty + have;
                if (currentQty >= c.maxLvl)
                    continue; // respect cap
                if (remaining < c.price)
                    continue; // can't afford this item

                // buy exactly 1 unit for this candidate
                remaining -= c.price;
                bought.merge(c.p.getId(), 1, Integer::sum);
                purchasedThisPass = true;

                if (remaining < minTopPrice)
                    break; // can't afford any top-3 now
            }

            if (!purchasedThisPass)
                break; // stuck (caps/prices)
        }

        return bought;
    }
}

class NumberToWordsConverter {
    private static final String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
            "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };
    private static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    public static String convert(long n) {
        if (n == 0)
            return "Zero";
        return convertChunk(n).trim();
    }

    private static String convertChunk(long n) {
        if (n < 20)
            return units[(int) n];
        if (n < 100)
            return tens[(int) n / 10] + " " + units[(int) n % 10];
        if (n < 1000)
            return units[(int) n / 100] + " Hundred " + convertChunk(n % 100);
        if (n < 100000)
            return convertChunk(n / 1000) + " Thousand " + convertChunk(n % 1000);
        if (n < 10000000)
            return convertChunk(n / 100000) + " Lakh " + convertChunk(n % 100000);
        return convertChunk(n / 10000000) + " Crore " + convertChunk(n % 10000000);
    }
}
