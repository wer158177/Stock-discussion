package com.hangha.controller.dto;

public record VerificationStatusRequest(
         Long userId,
        boolean isVerified
        ) {
}
