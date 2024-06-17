package com.kosta.legolego.user.service;


import com.kosta.legolego.user.config.jwt.TokenProvider;
import com.kosta.legolego.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사에 실패하면 예외 발생
        if(!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        String userEmail = refreshTokenService.findByRefreshToken(refreshToken).getUserEmail();
        SiteUser siteUser = userService.findByUserEmail(userEmail);

        return tokenProvider.generateToken(siteUser, Duration.ofHours(2));
    }
}