package com.hangha.stockdiscussion.User.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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


}