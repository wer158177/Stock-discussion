package com.hangha.stockservice.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CandleRequestDto {
    private String market; // KRW-BTC, KRW-ETH 등
    private String interval; // daily, weekly, monthly, yearly
    private LocalDateTime startDate; // 조회 시작 날짜
    private LocalDateTime endDate; // 조회 종료 날짜
}
