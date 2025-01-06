package com.hangha.stockservice.domain.repository;

import com.hangha.stockservice.domain.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByActiveTrue();
}
