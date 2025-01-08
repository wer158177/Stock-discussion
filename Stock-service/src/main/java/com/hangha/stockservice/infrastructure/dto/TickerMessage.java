package com.hangha.stockservice.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TickerMessage {
    private String type;
    private String code;

    @JsonProperty("opening_price")
    private double openingPrice;

    @JsonProperty("high_price")
    private double highPrice;

    @JsonProperty("low_price")
    private double lowPrice;

    @JsonProperty("trade_price")
    private double tradePrice;

    @JsonProperty("prev_closing_price")
    private double prevClosingPrice;

    @JsonProperty("acc_trade_price")
    private double accTradePrice;

    private String change;

    @JsonProperty("change_price")
    private double changePrice;

    @JsonProperty("signed_change_price")
    private double signedChangePrice;

    @JsonProperty("change_rate")
    private double changeRate;

    @JsonProperty("signed_change_rate")
    private double signedChangeRate;

    @JsonProperty("ask_bid")
    private String askBid;

    @JsonProperty("trade_volume")
    private double tradeVolume;

    @JsonProperty("acc_trade_volume")
    private double accTradeVolume;

    @JsonProperty("trade_date")
    private String tradeDate;

    @JsonProperty("trade_time")
    private String tradeTime;

    @JsonProperty("trade_timestamp")
    private long tradeTimestamp;

    @JsonProperty("acc_ask_volume")
    private double accAskVolume;

    @JsonProperty("acc_bid_volume")
    private double accBidVolume;

    @JsonProperty("highest_52_week_price")
    private double highest52WeekPrice;

    @JsonProperty("highest_52_week_date")
    private String highest52WeekDate;

    @JsonProperty("lowest_52_week_price")
    private double lowest52WeekPrice;

    @JsonProperty("lowest_52_week_date")
    private String lowest52WeekDate;

    @JsonProperty("market_state")
    private String marketState;

    @JsonProperty("is_trading_suspended")
    private boolean isTradingSuspended;

    @JsonProperty("delisting_date")
    private String delistingDate;

    @JsonProperty("market_warning")
    private String marketWarning;

    private long timestamp;

    @JsonProperty("acc_trade_price_24h")
    private double accTradePrice24h;

    @JsonProperty("acc_trade_volume_24h")
    private double accTradeVolume24h;

    @JsonProperty("stream_type")
    private String streamType;
}