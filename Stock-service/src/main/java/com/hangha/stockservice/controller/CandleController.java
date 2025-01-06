package com.hangha.stockservice.controller;

import com.hangha.stockservice.batch.CandleBatchJob;
import com.hangha.stockservice.controller.dto.CandleResponseDto;
import com.hangha.stockservice.domain.service.CandleService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/candles")
public class CandleController {

    private final CandleBatchJob candleBatchJob;
    private final CandleService candleService;

    public CandleController(CandleBatchJob candleBatchJob, CandleService candleService) {
        this.candleBatchJob = candleBatchJob;
        this.candleService = candleService;
    }

    @PostMapping("/fetch")
    public String fetchCandleData(@RequestParam(required = false) String startDate,
                                  @RequestParam(required = false) String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusYears(5);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        candleBatchJob.fetchCandleData(start, end);
        return "Candle data fetch completed from " + start + " to " + end;
    }


    // 일봉 데이터 조회
    @GetMapping("/daily")
    public List<CandleResponseDto> getDailyCandles(@RequestParam String market, @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        return candleService.getDailyCandles(market, startDate, endDate);
    }

    // 주봉 데이터 조회
    @GetMapping("/weekly")
    public List<CandleResponseDto> getWeeklyCandles(@RequestParam String market, @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        return candleService.getWeeklyCandles(market, startDate, endDate);
    }

    // 월봉 데이터 조회
    @GetMapping("/monthly")
    public List<CandleResponseDto> getMonthlyCandles(@RequestParam String market, @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        return candleService.getMonthlyCandles(market, startDate, endDate);
    }

    // 연봉 데이터 조회
    @GetMapping("/yearly")
    public List<CandleResponseDto> getYearlyCandles(@RequestParam String market, @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        return candleService.getYearlyCandles(market, startDate, endDate);
    }
}
