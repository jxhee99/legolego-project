package com.kosta.legolego.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {
        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_num")
    private long id;

    @Column(name = "user_email", nullable = false, unique = true)
    private String userEmail;

    @Column(name="refresh_token", nullable = false)
    private String refreshToken;

    public RefreshToken(String userEmail, String refreshToken){
        this.userEmail=userEmail;
        this.refreshToken=refreshToken;
    }

    public RefreshToken update(String newRefreshToken){
        this.refreshToken=newRefreshToken;
        return this;
    }

}
