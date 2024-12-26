package com.hangha.userservice.application.command;



public record UpdateProfileCommand(
        String username,
        String intro,
        String imageUrl
) {}