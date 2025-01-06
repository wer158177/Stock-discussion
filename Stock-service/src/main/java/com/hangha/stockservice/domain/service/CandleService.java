package com.hangha.stockservice.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.stockservice.controller.dto.CandleResponseDto;
import com.hangha.stockservice.domain.entity.Candle;
import com.hangha.stockservice.domain.entity.Stock;
import com.hangha.stockservice.domain.repository.CandleRepository;
import com.hangha.stockservice.infrastructure.UpbitService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandleService {

    private final UpbitService upbitService;
    private final CandleRepository candleRepository;
    private final ObjectMapper objectMapper;

    public CandleService(UpbitService upbitService, CandleRepository candleRepository) {
        this.upbitService = upbitService;
        this.candleRepository = candleRepository;
        this.objectMapper = new ObjectMapper();
    }

    public void saveCandleData(String interval, Stock stock, LocalDate startDate, LocalDate endDate) {
        LocalDate current = endDate;

        System.out.printf("Start fetching candle data for stock: %s, interval: %s, from: %s to: %s%n",
                stock.getMarket(), interval, startDate, endDate);

        while (!current.isBefore(startDate)) {
            String to = current + "T00:00:00Z";
            System.out.printf("Fetching data for interval: %s, market: %s, to: %s%n", interval, stock.getMarket(), to);

            String candleData = upbitService.getCandleData(interval, stock.getMarket(), to, 200);

            try {
                JsonNode candles = objectMapper.readTree(candleData);
                Iterator<JsonNode> iterator = candles.iterator();

                int processedCount = 0;
                while (iterator.hasNext()) {
                    JsonNode candleNode = iterator.next();

                    Candle candle = Candle.builder()
                            .stock(stock)
                            .interval(interval)
                            .candleDateTimeUtc(LocalDateTime.parse(candleNode.get("candle_date_time_utc").asText()))
                            .candleDateTimeKst(LocalDateTime.parse(candleNode.get("candle_date_time_kst").asText()))
                            .openingPrice(BigDecimal.valueOf(candleNode.get("opening_price").asDouble()))
                            .highPrice(BigDecimal.valueOf(candleNode.get("high_price").asDouble()))
                            .lowPrice(BigDecimal.valueOf(candleNode.get("low_price").asDouble()))
                            .tradePrice(BigDecimal.valueOf(candleNode.get("trade_price").asDouble()))
                            .timestamp(candleNode.get("timestamp").asLong())
                            .candleAccTradePrice(BigDecimal.valueOf(candleNode.get("candle_acc_trade_price").asDouble()))
                            .candleAccTradeVolume(BigDecimal.valueOf(candleNode.get("candle_acc_trade_volume").asDouble()))
                            .build();

                    if (!candleRepository.existsByStockAndCandleDateTimeKstAndInterval(stock, candle.getCandleDateTimeKst(), interval)) {
                        candleRepository.save(candle);
                        processedCount++;
                    }
                }
                System.out.printf("Processed %d candles for interval: %s, market: %s%n", processedCount, interval, stock.getMarket());
            } catch (Exception e) {
                System.err.printf("Error processing candle data for interval: %s, market: %s, error: %s%n",
                        interval, stock.getMarket(), e.getMessage());
                e.printStackTrace();
            }

            current = switch (interval) {
                case "days" -> current.minusDays(200);
                case "weeks" -> current.minusWeeks(200);
                case "months" -> current.minusMonths(200);
                case "years" -> current.minusYears(200);
                default -> throw new IllegalArgumentException("Unsupported interval: " + interval);
            };
        }
        System.out.printf("Finished fetching candle data for stock: %s, interval: %s%n", stock.getMarket(), interval);
    }


    // 특정 기간 내의 일봉 데이터 조회
    public List<CandleResponseDto> getDailyCandles(String market, LocalDateTime startDate, LocalDateTime endDate) {
        List<Candle> candles = candleRepository.findByStock_MarketAndCandleDateTimeKstBetweenAndInterval(
                market, startDate, endDate, "days");
        return mapToCandleResponseDto(candles);
    }

    // 주봉 데이터 조회
    public List<CandleResponseDto> getWeeklyCandles(String market, LocalDateTime startDate, LocalDateTime endDate) {
        List<Candle> candles = candleRepository.findByStock_MarketAndCandleDateTimeKstBetweenAndInterval(
                market, startDate, endDate, "weeks");
        return mapToCandleResponseDto(candles);
    }

    // 월봉 데이터 조회
    public List<CandleResponseDto> getMonthlyCandles(String market, LocalDateTime startDate, LocalDateTime endDate) {
        List<Candle> candles = candleRepository.findByStock_MarketAndCandleDateTimeKstBetweenAndInterval(
                market, startDate, endDate, "months");
        return mapToCandleResponseDto(candles);
    }

    // 연봉 데이터 조회
    public List<CandleResponseDto> getYearlyCandles(String market, LocalDateTime startDate, LocalDateTime endDate) {
        List<Candle> candles = candleRepository.findByStock_MarketAndCandleDateTimeKstBetweenAndInterval(
                market, startDate, endDate, "years");
        return mapToCandleResponseDto(candles);
    }

    // 엔티티를 DTO로 변환하는 메서드
    private List<CandleResponseDto> mapToCandleResponseDto(List<Candle> candles) {
        return candles.stream()
                .map(CandleResponseDto::fromEntity)  // fromEntity 메서드를 사용하여 변환
                .collect(Collectors.toList());
    }
}








