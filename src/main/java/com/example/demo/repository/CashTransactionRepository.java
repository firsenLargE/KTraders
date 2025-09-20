package com.example.demo.repository;

import com.example.demo.entity.CashTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CashTransactionRepository extends JpaRepository<CashTransaction, Long> {
    List<CashTransaction> findByOccurredAtBetween(LocalDateTime start, LocalDateTime end);
}


