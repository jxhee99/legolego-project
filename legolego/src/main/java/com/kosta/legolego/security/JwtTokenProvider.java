package com.kosta.legolego.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenProvider {

//    private final String SECRET_KEY = "mySecretKey";
//     private final long VALIDITY_IN_MS = 3600000;  // 1시간

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    private final long ACCESS_TOKEN_VALIDITY = 3600000;  // 1시간

    // Jwt 토큰 생성
    public String createToken(UserDetails userDetails) {
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.put("roles", userDetails.getAuthorities());

        Date now = new Date();
        Date validity = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
