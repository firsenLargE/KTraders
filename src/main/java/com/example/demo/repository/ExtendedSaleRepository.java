package com.example.demo.repository;

import com.example.demo.dto.ProductSalesRow;
import com.example.demo.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ExtendedSaleRepository extends JpaRepository<Sale, Integer> {
    @Query("""
              select new com.example.demo.dto.ProductSalesRow(
                 s.product.id,
                 s.product.name,
                 SUM(COALESCE(s.quantity, 0)),
                 SUM(COALESCE(s.totalPrice, 0.0)),
                 SUM(COALESCE(s.totalPrice, 0.0)) * 0.13,
                 SUM(COALESCE(s.totalPrice, 0.0)) * 1.13
              )
              from Sale s
              where s.date >= :start and s.date < :endExclusive
              group by s.product.id, s.product.name
              order by s.product.name
            """)
    List<ProductSalesRow> aggregateByProduct(@Param("start") LocalDate start, @Param("endExclusive") LocalDate endExclusive);
}


