package com.hangha.stockservice.batch;

import com.hangha.stockservice.domain.entity.Stock;
import com.hangha.stockservice.domain.repository.StockRepository;
import com.hangha.stockservice.domain.service.CandleService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CandleBatchJob {

    private final StockRepository stockRepository;
    private final CandleService candleService;

    public CandleBatchJob(StockRepository stockRepository, CandleService candleService) {
        this.stockRepository = stockRepository;
        this.candleService = candleService;
    }

    /**
     * 수동으로 활성화된 종목의 캔들 데이터를 저장
     */
    public void fetchCandleData(LocalDate startDate, LocalDate endDate) {
        List<Stock> activeStocks = stockRepository.findByActiveTrue();

        if (activeStocks.isEmpty()) {
            System.out.println("활성화된 종목이 없습니다.");
            return;
        }

        for (Stock stock : activeStocks) {
            fetchCandleDataForStock(stock, startDate, endDate);
        }

        System.out.println("모든 활성화된 종목의 캔들 데이터 저장 완료.");
    }

    /**
     * 특정 종목의 캔들 데이터를 지정된 기간에 따라 저장
     */
    private void fetchCandleDataForStock(Stock stock, LocalDate startDate, LocalDate endDate) {
        String[] intervals = {"days", "weeks", "months", "years"};
        for (String interval : intervals) {
            candleService.saveCandleData(interval, stock, startDate, endDate);
        }
        System.out.printf("[%s] 종목의 캔들 데이터 저장 완료.%n", stock.getMarket());
    }
}
