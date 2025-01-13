package com.hangha.mvclivechatservice.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TestAppender extends AppenderBase<ILoggingEvent> {
    private final List<String> logs = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent eventObject) {
        System.out.println("Captured log: " + eventObject.getFormattedMessage());
        logs.add(eventObject.getFormattedMessage());
    }

}
