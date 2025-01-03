package com.hangha.userservice.controller;

import com.hangha.userservice.application.FollowingApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/following")
public class FollowingController {

    private final FollowingApplicationService followingApplicationService;

    public FollowingController(FollowingApplicationService followingApplicationService) {
        this.followingApplicationService = followingApplicationService;
    }

    @PostMapping("/{followingId}/follow")
    public ResponseEntity<Void> followUser(
            @RequestHeader("X-Claim-userId") Long userId,
            @PathVariable Long followingId) {
        followingApplicationService.followUser(userId, followingId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @DeleteMapping("/{followingId}/unfollow")
    public ResponseEntity<Void> unfollowUser(
            @RequestHeader("X-Claim-userId") Long userId,
            @PathVariable Long followingId) {
        followingApplicationService.unfollowUser(userId, followingId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<Long>> getFollowers(@PathVariable("userId") Long userId,
                                                   @RequestParam("cursor") Long cursor,
                                                   @RequestParam("size") int size) {
        List<Long> followers = followingApplicationService.getFollowers(userId, cursor, size);
        return ResponseEntity.ok(followers);
    }


    @GetMapping("/{userId}/following")
    public ResponseEntity<List<Long>> getFollowing(@PathVariable Long userId) {
        List<Long> following = followingApplicationService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }
}
