package com.example.demo.config;

import com.example.demo.entity.CashTransaction;
import com.example.demo.entity.Purchase;
import com.example.demo.entity.Sale;
import com.example.demo.repository.CashTransactionRepository;
import com.example.demo.repository.PurchaseRepository;
import com.example.demo.repository.SaleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class LedgerMigration {

    @Bean
    CommandLineRunner initLedger(SaleRepository saleRepo,
            PurchaseRepository purchaseRepo,
            CashTransactionRepository txRepo) {
        return args -> {
            System.out.println("Starting Ledger Migration...");

            // 1. Backfill Sales
            List<Sale> sales = saleRepo.findAll();
            int salesAdded = 0;
            for (Sale s : sales) {
                String ref = "SALE:" + s.getId();
                if (!txRepo.existsByReference(ref)) {
                    CashTransaction tx = new CashTransaction();
                    tx.setType(CashTransaction.Type.IN);
                    tx.setAmount(BigDecimal.valueOf(s.getTotalPrice()));
                    tx.setPaymentMethod(CashTransaction.PaymentMethod.CASH);
                    tx.setReference(ref);
                    tx.setCounterparty(s.getCustomer() != null ? s.getCustomer().getName() : "Walk-in Customer");
                    tx.setOccurredAt(s.getDate() != null ? s.getDate().atStartOfDay() : java.time.LocalDateTime.now());
                    tx.setNotes("Backfilled Sale of " + (s.getProduct() != null ? s.getProduct().getName() : "Item"));
                    tx.setSettled(true); // Assume historical sales are settled
                    txRepo.save(tx);
                    salesAdded++;
                }
            }
            System.out.println("Backfilled " + salesAdded + " sales transactions.");

            // 2. Backfill Purchases
            List<Purchase> purchases = purchaseRepo.findAll();
            int purchasesAdded = 0;
            for (Purchase p : purchases) {
                String ref = "PURCHASE:" + p.getId();
                if (!txRepo.existsByReference(ref)) {
                    CashTransaction tx = new CashTransaction();
                    tx.setType(CashTransaction.Type.OUT);
                    tx.setAmount(p.getTotalCost());
                    tx.setPaymentMethod(CashTransaction.PaymentMethod.CASH);
                    tx.setReference(ref);
                    tx.setCounterparty(p.getSupplier());
                    tx.setOccurredAt(p.getPurchaseDate() != null ? p.getPurchaseDate().atStartOfDay()
                            : java.time.LocalDateTime.now());
                    tx.setNotes("Backfilled Purchase: " + p.getInvoiceNumber());
                    tx.setSettled(true); // Assume historical purchases are settled
                    txRepo.save(tx);
                    purchasesAdded++;
                }
            }
            System.out.println("Backfilled " + purchasesAdded + " purchase transactions.");
        };
    }
}
