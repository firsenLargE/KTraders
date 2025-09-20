package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class CashTransaction {
    public enum Type { IN, OUT }
    public enum PaymentMethod { CASH, CREDIT, CHEQUE, UPI, BANK_TRANSFER, REMAINING }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    private boolean settled = true;

    private String reference;
    private String counterparty;
    private String notes;
    private LocalDateTime occurredAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public boolean isSettled() { return settled; }
    public void setSettled(boolean settled) { this.settled = settled; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getCounterparty() { return counterparty; }
    public void setCounterparty(String counterparty) { this.counterparty = counterparty; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}


