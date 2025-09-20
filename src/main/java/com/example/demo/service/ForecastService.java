package com.example.demo.service;

import com.example.demo.entity.Forecast;
import java.time.LocalDate;
import java.util.List;

public interface ForecastService {
    void generateForecast(Integer productId);
    List<Forecast> getForecastsByProduct(Integer productId);
    List<Forecast> getSeasonalForecasts(String season);
    void generateAllProductForecasts();
    Forecast getForecastForProductAndDate(Integer productId, LocalDate forecastFor);
}


