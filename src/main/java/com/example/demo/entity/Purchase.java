package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String supplier;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    @Column(nullable = false)
    private Integer quantity;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal unitCost;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal totalCost;

    private String invoiceNumber;
    private String paymentMethod;
    private String notes;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Purchase() {}

    public Purchase(Product product, String supplier, LocalDate purchaseDate, Integer quantity, 
                   BigDecimal unitCost, String invoiceNumber, String paymentMethod, String notes) {
        this.product = product;
        this.supplier = supplier;
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = unitCost.multiply(BigDecimal.valueOf(quantity));
        this.invoiceNumber = invoiceNumber;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        if (this.unitCost != null) {
            this.totalCost = this.unitCost.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { 
        this.unitCost = unitCost;
        if (this.quantity != null) {
            this.totalCost = unitCost.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }


    @Transient
    public int getMonth() {
        return purchaseDate != null ? purchaseDate.getMonthValue() : 0;
    }
}
