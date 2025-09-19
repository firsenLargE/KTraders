package com.example.demo.service.impl;

import com.example.demo.entity.Forecast;
import com.example.demo.entity.Product;
import com.example.demo.entity.Sale;
import com.example.demo.repository.ForecastRepository;
import com.example.demo.service.ForecastService;
import com.example.demo.service.ProductService;
import com.example.demo.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class ForecastServiceImpl implements ForecastService {
    @Autowired private ForecastRepository forecastRepository;
    @Autowired private SaleService saleService;
    @Autowired private ProductService productService;

    public void generateForecast(Integer productId) {
        Product product = productService.getProductById(productId);
        if (product == null) return;

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(12);
        List<Sale> historicalSales = saleService.getSalesByProductAndDateRange(productId, startDate, endDate);

        List<Monthly> series = toMonthlySeries(historicalSales, startDate.withDayOfMonth(1), endDate.withDayOfMonth(1));
        Decomp d = fitTrendSeason(series);

        YearMonth lastYM = YearMonth.from(endDate.withDayOfMonth(1));
        int lastT = series.isEmpty() ? 0 : series.get(series.size()-1).t;

        for (int i=1; i<=3; i++) {
            YearMonth ym = lastYM.plusMonths(i);
            int tFuture = lastT + i;
            int monthIdx = ym.getMonthValue()-1;

            Integer predictedDemand = predictMonth(d, tFuture, monthIdx);
            Double confidence = confidenceFromMape(d.mape);

            String season = getSeason(ym.atDay(1));
            LocalDate forecastFor = ym.atDay(1);

            Optional<Forecast> existing = forecastRepository.findByProductAndForecastFor(productId, forecastFor);
            Forecast f = existing.orElseGet(() -> new Forecast(product, season, predictedDemand, confidence, forecastFor, "TREND_SEASONAL"));

            f.setPredictedDemand(predictedDemand);
            f.setConfidence(confidence);
            f.setSeason(season);
            f.setMethod("TREND_SEASONAL");
            f.setAccuracy(BigDecimal.valueOf(1.0 - Math.min(1.0, d.mape)).setScale(2, RoundingMode.HALF_UP).doubleValue());
            forecastRepository.save(f);
        }
    }
    public List<Forecast> getForecastsByProduct(Integer productId) { return forecastRepository.findByProductId(productId); }
    public List<Forecast> getSeasonalForecasts(String season) { return forecastRepository.findBySeason(season); }
    public void generateAllProductForecasts() { productService.getAllProducts().forEach(p -> generateForecast(p.getId())); }
    public Forecast getForecastForProductAndDate(Integer productId, LocalDate forecastFor) {
        return forecastRepository.findByProductAndForecastFor(productId, forecastFor).orElse(null);
    }

    private String getSeason(LocalDate date) {
        switch (date.getMonth()) {
            case DECEMBER, JANUARY, FEBRUARY: return "WINTER";
            case MARCH, APRIL, MAY: return "SPRING";
            case JUNE, JULY, AUGUST: return "SUMMER";
            case SEPTEMBER, OCTOBER, NOVEMBER: return "AUTUMN";
            default: return "UNKNOWN";
        }
    }
    private static final class Monthly { final int t, qty, monthIndex; Monthly(int t,int qty,int monthIndex){this.t=t;this.qty=qty;this.monthIndex=monthIndex;} }
    private static final int MIN_TRAIN_MONTHS = 6;

    private List<Monthly> toMonthlySeries(List<Sale> sales, LocalDate startFirst, LocalDate endFirst) {
        YearMonth ymStart = YearMonth.from(startFirst);
        YearMonth ymEnd = YearMonth.from(endFirst);
        int len = (int) java.time.temporal.ChronoUnit.MONTHS.between(ymStart, ymEnd) + 1;

        Map<YearMonth, Integer> totals = new HashMap<>();
        for (Sale s : sales) {
            if (s.getDate()==null || s.getQuantity()==null) continue;
            YearMonth ym = YearMonth.from(s.getDate().withDayOfMonth(1));
            if (ym.isBefore(ymStart) || ym.isAfter(ymEnd)) continue;
            totals.merge(ym, s.getQuantity(), Integer::sum);
        }

        List<Monthly> out = new ArrayList<>(len);
        for (int i=0;i<len;i++){
            YearMonth ym = ymStart.plusMonths(i);
            int qty = totals.getOrDefault(ym, 0);
            int monthIdx = ym.getMonthValue()-1;
            out.add(new Monthly(i+1, qty, monthIdx));
        }
        return out;
    }

    private static final class Decomp { double intercept; double slope; double[] season = new double[12]; double mape; }

    private Decomp fitTrendSeason(List<Monthly> series) {
        Decomp d = new Decomp();
        int n = series.size();
        if (n < MIN_TRAIN_MONTHS) {
            double mean = series.stream().mapToInt(m -> m.qty).average().orElse(5.0);
            d.intercept = mean; d.slope = 0.0; java.util.Arrays.fill(d.season, 1.0); d.mape = 0.40; return d;
        }
        double sumT=0,sumY=0,sumTT=0,sumTY=0;
        for (Monthly m : series) { sumT+=m.t; sumY+=m.qty; sumTT+=m.t*m.t; sumTY+=m.t*m.qty; }
        double b = (n*sumTY - sumT*sumY) / Math.max(1e-9, (n*sumTT - sumT*sumT));
        double a = (sumY - b*sumT)/n;

        double[] monthSum = new double[12]; int[] monthCnt=new int[12];
        for (Monthly m: series){
            double trend = a + b*m.t; double ratio = (trend<=1e-9 ? 1.0 : m.qty/Math.max(1.0, trend));
            monthSum[m.monthIndex]+=ratio; monthCnt[m.monthIndex]+=1;
        }
        double sumIdx=0;
        for (int k=0;k<12;k++){ d.season[k] = (monthCnt[k]==0?1.0:monthSum[k]/monthCnt[k]); sumIdx+=d.season[k]; }
        double meanIdx = sumIdx/12.0; if (meanIdx!=0) for (int k=0;k<12;k++) d.season[k]/=meanIdx;
        d.intercept=a; d.slope=b;

        double apeSum=0; int apeN=0;
        for (Monthly m: series){
            double base=a+b*m.t; double pred=Math.max(0.0, base)*d.season[m.monthIndex]; double y=m.qty;
            if (y>0){ apeSum += Math.abs(y-pred)/y; apeN++; }
        }
        d.mape = (apeN==0 ? 0.50 : apeSum/apeN); return d;
    }

    private int predictMonth(Decomp d, int tFuture, int monthIndex) {
        double base = d.intercept + d.slope * tFuture;
        double pred = Math.max(0.0, base) * d.season[monthIndex];
        return Math.max(1, (int)Math.round(pred));
    }
    private double confidenceFromMape(double mape) {
        double c = 1.0 - Math.max(0.0, Math.min(1.0, mape));
        return BigDecimal.valueOf(0.5 + 0.45*c).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}


