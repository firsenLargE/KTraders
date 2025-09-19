package com.example.demo.service;

import com.example.demo.entity.CashTransaction;
import com.example.demo.repository.CashTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class CashLedgerService {
    private final CashTransactionRepository repo;
    @Autowired CashLedgerService(CashTransactionRepository repo) { this.repo = repo; }

    public static class Result {
        public List<CashTransaction> rows;
        public BigDecimal totalIn;
        public BigDecimal totalOut;
        public BigDecimal net;
        public Map<CashTransaction.PaymentMethod, BigDecimal> totalsByMethod;
        public BigDecimal remaining;
    }

    public List<CashTransaction> range(LocalDate from, LocalDate to) {
        return repo.findByOccurredAtBetween(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
    }

    public CashTransaction record(CashTransaction tx) { return repo.save(tx); }

    @Transactional(readOnly = true)
    public Result view(LocalDate from, LocalDate to,
                Set<CashTransaction.Type> types,
                Set<CashTransaction.PaymentMethod> methods,
                Boolean settledOnly) {
        List<CashTransaction> all = repo.findByOccurredAtBetween(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        List<CashTransaction> filtered = all.stream()
            .filter(tx -> types==null || types.isEmpty() || types.contains(tx.getType()))
            .filter(tx -> methods==null || methods.isEmpty() || methods.contains(tx.getPaymentMethod()))
            .filter(tx -> settledOnly==null || (tx.isSettled()==settledOnly))
            .sorted((a,b) -> a.getOccurredAt().compareTo(b.getOccurredAt()))
            .toList();

        BigDecimal in  = filtered.stream().filter(tx -> tx.getType()==CashTransaction.Type.IN)
            .map(CashTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal out = filtered.stream().filter(tx -> tx.getType()==CashTransaction.Type.OUT)
            .map(CashTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<CashTransaction.PaymentMethod, BigDecimal> byMethod = new HashMap<>();
        for (CashTransaction tx: filtered) {
            byMethod.merge(tx.getPaymentMethod(), tx.getAmount(), BigDecimal::add);
        }

        BigDecimal remaining = filtered.stream()
            .filter(tx -> tx.getPaymentMethod()==CashTransaction.PaymentMethod.CREDIT && !tx.isSettled())
            .map(CashTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        Result r = new Result();
        r.rows = filtered;
        r.totalIn = in;
        r.totalOut = out;
        r.net = in.subtract(out);
        r.totalsByMethod = byMethod;
        r.remaining = remaining;
        return r;
    }
}


