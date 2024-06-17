package com.kosta.legolego.config.jwt;

import com.kosta.legolego.user.config.jwt.JwtProperties;
import com.kosta.legolego.user.config.jwt.TokenProvider;
import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.UserRepository;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    // generateToken() 검증 테스트
    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // given : 토큰에 유저 정보를 추가하기 위한 테스트 유저를 만든다.
        String uniqueEmail = UUID.randomUUID().toString() + "@mail.com";

        User testUser = userRepository.save(User.builder()
                .userEmail(uniqueEmail)
                .userPw("test")
                .username("testUser")
                .userNickname("nickname")
                .userPhone("010-1234-5678")
                .build());

        // when : 토큰 제공자의 generateToken() 메소드를 호출해 토큰을 만든다.
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // then : jjwt 라이브러리를 사용해 토큰을 복호화한다.
        //        토큰을 만들 때 클레임으로 넣어둔 userEmail 값이 given 절에서 만든 유저 이메일과 동일한지 확인!
        String userEmail = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // subject를 가져오면 userEmail과 동일

        assertThat(userEmail).isEqualTo(testUser.getUserEmail());
    }

    // validToken() 검증 테스트
    @DisplayName("validToken(): 만료된 토큰인 경우에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // given : 토큰 제공자의 generateToken() 메소드를 호출해 유효한 토큰을 생성한다.
        String token = tokenProvider.generateToken(createTestUser(), Duration.ofDays(14));

        // when : 만료 시간을 현재 시간부터 7일 전으로 설정하여 유효하지 않은 토큰을 생성한다.
        token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .setSubject("test@mail.com")
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();

        // then : 토큰 제공자의 validToken() 메소드를 호출해 유효한 토큰인지 검증한 뒤 결과값을 반환받는다.
        boolean result = tokenProvider.validToken(token);

        // then : 반환값이 false(유효한 토큰이 아님)인지 확인한다.
        assertThat(result).isFalse();
    }

    @DisplayName("validToken(): 유효한 토큰인 경우에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        // given
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);

        // when
        boolean result = tokenProvider.validToken(token);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("getAuthentication(): 토큰 기반으로 인증정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        // given
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        // when
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }
    // 테스트용 사용자 생성 메소드
    private User createTestUser() {
        String uniqueEmail = UUID.randomUUID().toString() + "@mail.com";
        return userRepository.save(User.builder()
                .userEmail(uniqueEmail)
                .userPw("test")
                .username("testUser")
                .userNickname("nickname")
                .userPhone("010-1234-5678")
                .build());
    }

    // getUserId() 검증 테스트
    @DisplayName("getUserId() : 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        // given : 토큰 제공자의 generateToken() 메소드를 호출해 토큰을 만든다.
        User testUser = createTestUser();
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // when : 토큰 제공자의 getUserId() 메소드를 호출해 유저 ID를 반환받는다.
        Long userIdByToken = tokenProvider.getUserId(token);

        // then : 반환받은 유저 ID가 given 절에서 설정한 유저 ID와 같은지 확인한다.
       // assertThat(userIdByToken).isEqualTo(testUser.getId());
        assertThat(userIdByToken).isEqualTo(testUser.getUserNum());

    }
}
