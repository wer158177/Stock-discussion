package com.hangha.application.command;



public record UpdateProfileCommand(
        String username,
        String intro,
        String imageUrl
) {}