package com.example.demo.repository;

import com.example.demo.entity.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ForecastRepository extends JpaRepository<Forecast, Long> {
    List<Forecast> findByProductId(Integer productId);
    List<Forecast> findByForecastForBetween(LocalDate startDate, LocalDate endDate);
    List<Forecast> findBySeason(String season);
    @Query("SELECT f FROM Forecast f WHERE f.product.id = ?1 AND f.forecastFor = ?2")
    Optional<Forecast> findByProductAndForecastFor(Integer productId, LocalDate forecastFor);
}


