package com.hangha.stockdiscussion.User.infrastructure.security.domain;

import com.hangha.stockdiscussion.User.domain.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;  // 토큰 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;   // 유저 식별자

    private String refreshToken;  // 리프레시 토큰


    private Date tokenExpiration;  // 토큰 만료 날짜

    private Date createdAt;  // 토큰 생성 날짜

    private String deviceInfo;


    public RefreshToken(User user, String refreshToken, Date tokenExpiration, Date createdAt) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.tokenExpiration = tokenExpiration;
        this.createdAt = createdAt;
    }
}
