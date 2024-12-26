package com.hangha.userservice.controller;

import com.hangha.userservice.application.FollowingApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/following")
public class FollowingController {

    private final FollowingApplicationService followingApplicationService;

    public FollowingController(FollowingApplicationService followingApplicationService) {
        this.followingApplicationService = followingApplicationService;
    }

    @PostMapping("/{followerId}/follow/{followingId}")
    public ResponseEntity<Void> followUser(
            @PathVariable Long followerId,
            @PathVariable Long followingId) {
        followingApplicationService.followUser(followerId, followingId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{followerId}/unfollow/{followingId}")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long followerId,
            @PathVariable Long followingId) {
        followingApplicationService.unfollowUser(followerId, followingId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<Long>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followingApplicationService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<Long>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(followingApplicationService.getFollowing(userId));
    }
}
