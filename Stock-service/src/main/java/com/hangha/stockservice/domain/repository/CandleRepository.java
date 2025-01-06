package com.hangha.stockservice.domain.repository;

import com.hangha.stockservice.domain.entity.Candle;
import com.hangha.stockservice.domain.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CandleRepository extends JpaRepository<Candle, Long> {
    boolean existsByStockAndCandleDateTimeKstAndInterval(Stock stock, LocalDateTime candleDateTimeKst, String interval);



    List<Candle> findByStock_MarketAndCandleDateTimeKstBetweenAndInterval(String market, LocalDateTime startDate, LocalDateTime endDate, String interval);


}
