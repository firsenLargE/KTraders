package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "sales")
public class Sale {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne @JoinColumn(name = "customer_id")
    private Customer customer;

    private Integer quantity;
    private Double totalPrice;
    private LocalDate date;
    private Double unitSellingPrice;

    public Sale() {}
    public Sale(Product product, Integer quantity, Double totalPrice, LocalDate date) {
        this.product=product; this.quantity=quantity; this.totalPrice=totalPrice; this.date=date;
    }

    public Double getUnitSellingPrice() { return unitSellingPrice; }
    public void setUnitSellingPrice(Double unitSellingPrice) { this.unitSellingPrice = unitSellingPrice; }
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


