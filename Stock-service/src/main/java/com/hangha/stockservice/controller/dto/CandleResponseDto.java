package com.hangha.stockservice.controller.dto;

import com.hangha.stockservice.domain.entity.Candle;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class CandleResponseDto {

    private Long id;  // 엔티티의 ID
    private String market; // 시장 종류 (예: KRW-BTC)
    private String interval; // 캔들의 기간 (daily, weekly, monthly, yearly)
    private LocalDateTime candleDateTimeUtc; // UTC 기준 캔들 날짜 및 시간
    private LocalDateTime candleDateTimeKst; // KST 기준 캔들 날짜 및 시간
    private BigDecimal openingPrice; // 시가
    private BigDecimal highPrice; // 고가
    private BigDecimal lowPrice; // 저가
    private BigDecimal tradePrice; // 거래 가격
    private BigDecimal candleAccTradePrice; // 누적 거래 금액
    private BigDecimal candleAccTradeVolume; // 누적 거래량
    private Long timestamp; // Unix 타임스탬프

    // 엔티티 -> DTO 변환 메서드
    public static CandleResponseDto fromEntity(Candle candle) {
        return CandleResponseDto.builder()
                .id(candle.getId())
                .market(candle.getStock().getMarket())  // Stock에서 시장 구분 정보 가져옴
                .interval(candle.getInterval())  // 캔들 기간
                .candleDateTimeUtc(candle.getCandleDateTimeUtc())  // UTC 기준 시간
                .candleDateTimeKst(candle.getCandleDateTimeKst())  // KST 기준 시간
                .openingPrice(candle.getOpeningPrice())
                .highPrice(candle.getHighPrice())
                .lowPrice(candle.getLowPrice())
                .tradePrice(candle.getTradePrice())
                .candleAccTradePrice(candle.getCandleAccTradePrice())
                .candleAccTradeVolume(candle.getCandleAccTradeVolume())
                .timestamp(candle.getTimestamp())
                .build();
    }
}
