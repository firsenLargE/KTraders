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
           s.date,
           SUM(CAST(COALESCE(s.quantity, 0) AS Long)),
           SUM(COALESCE(s.totalPrice, 0.0))
        )
        from Sale s
        where s.date >= :start and s.date < :endExclusive
        group by s.product.id, s.product.name, s.date
        order by s.date DESC, s.product.name ASC
      """)
  List<ProductSalesRow> aggregateByProduct(@Param("start") LocalDate start,
      @Param("endExclusive") LocalDate endExclusive);
}
