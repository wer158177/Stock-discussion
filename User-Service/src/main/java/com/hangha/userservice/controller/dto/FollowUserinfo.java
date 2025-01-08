package com.hangha.userservice.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class FollowUserinfo {

    private Long id;
    private String username;
    private String imageUrl;
    private String intro;
    private Long followerCount;
    private Long followingCount;
    private boolean isFollowing;








}
