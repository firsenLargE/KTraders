package com.example.demo.service;

import com.example.demo.entity.StockMovement;
import com.example.demo.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StockMovementService {
    private final StockMovementRepository repo;
    @Autowired StockMovementService(StockMovementRepository repo) { this.repo = repo; }
    public List<StockMovement> byProduct(Integer pid) { return repo.findByProductIdOrderByOccurredAtDesc(pid); }
    public StockMovement record(StockMovement m) { return repo.save(m); }
}


