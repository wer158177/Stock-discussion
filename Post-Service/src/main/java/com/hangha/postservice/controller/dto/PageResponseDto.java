package com.hangha.postservice.controller.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
public class PageResponseDto<T> {
    private List<T> content;
    private Pageable pageable;
    private boolean last;
    private long totalElements;
    private int totalPages;

    public PageResponseDto(Page<T> page) {
        this.content = page.getContent();
        this.pageable = page.getPageable();
        this.last = page.isLast();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}