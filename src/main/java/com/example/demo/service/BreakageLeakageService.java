package com.example.demo.service;

import com.example.demo.entity.BreakageLeakage;
import com.example.demo.entity.StockMovement;
import com.example.demo.repository.BreakageLeakageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BreakageLeakageService {
    private final BreakageLeakageRepository repo;
    private final StockMovementService movementSvc;

    @Autowired
    public BreakageLeakageService(BreakageLeakageRepository repo, StockMovementService movementSvc) {
        this.repo = repo;
        this.movementSvc = movementSvc;
    }

    public BreakageLeakage record(BreakageLeakage b) {
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

    public List<BreakageLeakage> all() {
        return repo.findAll();
    }
}


