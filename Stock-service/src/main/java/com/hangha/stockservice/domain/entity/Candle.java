package com.hangha.stockservice.domain.entity;

import com.hangha.stockservice.domain.entity.Stock;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "candles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"stock_id", "candle_date_time_kst", "interval"})
})
public class Candle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false, length = 10)
    private String interval; // daily, weekly, monthly, yearly

    @Column(nullable = false)
    private LocalDateTime candleDateTimeUtc;

    @Column(nullable = false)
    private LocalDateTime candleDateTimeKst;

    private BigDecimal openingPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal tradePrice;

    private BigDecimal candleAccTradePrice;
    private BigDecimal candleAccTradeVolume;

    private Long timestamp;



    @Builder
    public Candle(Stock stock, String interval, LocalDateTime candleDateTimeUtc, LocalDateTime candleDateTimeKst,
                  BigDecimal openingPrice, BigDecimal highPrice, BigDecimal lowPrice, BigDecimal tradePrice,
                  BigDecimal candleAccTradePrice, BigDecimal candleAccTradeVolume, Long timestamp) {
        this.stock = stock;
        this.interval = interval;
        this.candleDateTimeUtc = candleDateTimeUtc;
        this.candleDateTimeKst = candleDateTimeKst;
        this.openingPrice = openingPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.tradePrice = tradePrice;
        this.candleAccTradePrice = candleAccTradePrice;
        this.candleAccTradeVolume = candleAccTradeVolume;
        this.timestamp = timestamp;
    }


}
