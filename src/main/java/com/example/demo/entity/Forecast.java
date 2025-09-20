package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name = "forecasts")
public class Forecast {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "product_id")
    private Product product;

    private String season;
    private Integer predictedDemand;
    private Double confidence;
    private LocalDate forecastDate;
    private LocalDate forecastFor;
    private String method;
    private Double accuracy;

    public Forecast() {}
    public Forecast(Product product, String season, Integer predictedDemand, Double confidence, LocalDate forecastFor, String method) {
        this.product=product; this.season=season; this.predictedDemand=predictedDemand; this.confidence=confidence;
        this.forecastDate=LocalDate.now(); this.forecastFor=forecastFor; this.method=method;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }
    public Integer getPredictedDemand() { return predictedDemand; }
    public void setPredictedDemand(Integer predictedDemand) { this.predictedDemand = predictedDemand; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public LocalDate getForecastDate() { return forecastDate; }
    public void setForecastDate(LocalDate forecastDate) { this.forecastDate = forecastDate; }
    public LocalDate getForecastFor() { return forecastFor; }
    public void setForecastFor(LocalDate forecastFor) { this.forecastFor = forecastFor; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }
}


