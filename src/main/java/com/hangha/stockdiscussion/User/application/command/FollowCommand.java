package com.hangha.stockdiscussion.User.application.command;

public record FollowCommand(Long followerId, Long followingId) {}