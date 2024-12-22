package com.hangha.stockdiscussion.User.domain.entity;

import com.hangha.stockdiscussion.User.infrastructure.security.domain.RefreshToken;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String intro;

    private LocalDateTime createdAt;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleEnum userRole = UserRoleEnum.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;


    @Column(nullable = false)
    private boolean isVerified = false; // 인증 상태


    @Builder
    public User(Long id ,String username, String password, String email, String intro, String imageUrl,UserRoleEnum userRole,LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.intro = intro;
        this.imageUrl = imageUrl;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }



    public void updateProfile(String newUsername, String newIntro, String newProfileImage) {
        System.out.println("Before Update: " + this.username);
        if (newUsername != null && !newUsername.isBlank()) {
            this.username = newUsername;
        }
        if (newIntro != null && !newIntro.isBlank()) {
            this.intro = newIntro;
        }
        if (newProfileImage != null && !newProfileImage.isBlank()) {
            this.imageUrl = newProfileImage;
        }
        System.out.println("After Update: " + this.username);
    }

    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
    }


    public void markAsVerified() {
        this.isVerified = true;
    }





}