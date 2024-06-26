package com.kosta.legolego.member.service;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.admin.repository.AdminRepository;
import com.kosta.legolego.member.dto.*;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.partner.repository.PartnerRepository;
import com.kosta.legolego.security.CustomUserDetailsService;
import com.kosta.legolego.security.JwtTokenProvider;
import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// 로그인 시 이메일과 비밀번호를 확인하고, JWT 토큰을 발급
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private EmailService emailService;
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public ResponseDto signup(SignupDto signupDto, String role) {
        if (!isNicknameAvailable(signupDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        if (!isEmailAvailable(signupDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (!signupDto.getPassword().equals(signupDto.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        switch (role) {
            case "USER":
                User user = new User();
                user.setUserEmail(signupDto.getEmail());
                user.setUserPw(passwordEncoder.encode(signupDto.getPassword()));
                user.setUserName(signupDto.getName());
                user.setUserNickname(signupDto.getNickname());
                user.setUserPhone(signupDto.getPhone());
                User savedUser = userRepository.save(user);
                return convertToDto(savedUser, role);

            case "ADMIN":
                Admin admin = new Admin();
                admin.setAdminEmail(signupDto.getEmail());
                admin.setAdminPw(passwordEncoder.encode(signupDto.getPassword()));
                admin.setAdminName(signupDto.getName());
                Admin savedAdmin = adminRepository.save(admin);
                return convertToDto(savedAdmin, role);

            case "PARTNER":
                Partner partner = new Partner();
                partner.setPartnerEmail(signupDto.getEmail());
                partner.setPartnerPw(passwordEncoder.encode(signupDto.getPassword()));
                partner.setCompanyName(signupDto.getCompanyName());
                partner.setPartnerPhone(signupDto.getPhone());
                Partner savedPartner = partnerRepository.save(partner);
                return convertToDto(savedPartner, role);
            default:
                throw new IllegalArgumentException("Invalid role");

        }
    }

    public String login(LoginDto loginDto) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
            String token = jwtTokenProvider.createToken(userDetails);

            String role = getRole(loginDto.getEmail());
            logger.info("Login successful for user: {} with role: {}", loginDto.getEmail(), role);

            return token;
        } catch (UsernameNotFoundException e) {
            logger.error("User not found: {}", loginDto.getEmail());
            throw new BadCredentialsException("가입되지 않은 정보입니다.");
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for user: {}", loginDto.getEmail());
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
    }


    public String getRole(String email) {
        if (adminRepository.existsByAdminEmail(email)) {
            logger.info("Role for user {}: ADMIN", email);
            return "ADMIN";
        } else if (partnerRepository.existsByPartnerEmail(email)) {
            logger.info("Role for user {}: PARTNER", email);
            return "PARTNER";
        } else if (userRepository.existsByUserEmail(email)) {
            logger.info("Role for user {}: USER", email);
            return "USER";
        } else {
            logger.error("Invalid email for role determination: {}", email);
            throw new IllegalArgumentException("Invalid email");
        }
    }

    public String logout() {
        return "로그아웃 되었습니다!";
    }

    // 유효성 검사
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByUserNickname(nickname);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByUserEmail(email) && !partnerRepository.existsByPartnerEmail(email) && !adminRepository.existsByAdminEmail(email);
    }

    // 아이디 찾기 - 일반 회원
    public String findUserEmail(String userName, String userPhone) {
        User user = userRepository.findByUserNameAndUserPhone(userName, userPhone);
        if (user != null) {
            return user.getUserEmail();
        } else {
            return null;
        }
    }

    // 아이디 찾기 - 여행사
    public String findPartnerEmail(String companyName, String partnerPhone) {
        Partner partner = partnerRepository.findByCompanyNameAndPartnerPhone(companyName, partnerPhone);
        if (partner != null) {
            return partner.getPartnerEmail();
        } else {
            return null;
        }
    }

    // 비밀번호 찾기
    private Map<String, String> tokenStore = new HashMap<>();  // 토큰 저장소

    @Transactional
    public ResponseEntity<String> requestPasswordReset(FindPasswordRequestDto findPasswordRequestDto) {
        User user = userRepository.findByUserEmailAndUserNameAndUserPhone(
                findPasswordRequestDto.getEmail(), findPasswordRequestDto.getName(), findPasswordRequestDto.getPhone());

        if (user != null) {
            String token = generateResetToken(user.getUserEmail());
            emailService.sendPasswordResetEmail(user.getUserEmail(), token);
            return ResponseEntity.ok("비밀번호 재설정 이메일이 발송되었습니다.");
        }

        Partner partner = partnerRepository.findByPartnerEmailAndCompanyNameAndPartnerPhone(
                findPasswordRequestDto.getEmail(), findPasswordRequestDto.getName(), findPasswordRequestDto.getPhone());

        if (partner != null) {
            String token = generateResetToken(partner.getPartnerEmail());
            emailService.sendPasswordResetEmail(partner.getPartnerEmail(), token);
            return ResponseEntity.ok("비밀번호 재설정 이메일이 발송되었습니다.");
        }

        return ResponseEntity.status(404).body("계정을 찾을 수 없습니다.");
    }

    private String generateResetToken(String email) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, email);  // 토큰과 이메일을 매핑하여 저장
        return token;
    }

    @Transactional
    public ResponseEntity<String> validateResetToken(String token) {
        if (isValidToken(token)) {
            return ResponseEntity.ok("유효한 토큰입니다.");
        }
        return ResponseEntity.status(400).body("유효하지 않은 토큰입니다.");
    }

    private boolean isValidToken(String token) {
        return tokenStore.containsKey(token);  // 토큰이 저장소에 존재하는지 확인
    }

    @Transactional
    public ResponseEntity<String> resetPassword(String token, ResetPasswordRequestDto resetPasswordRequestDto) {
        if (isValidToken(token)) {
            String email = tokenStore.get(token);
            User user = userRepository.findByUserEmail(email);
            if (user != null) {
                user.setUserPw(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));  // 비밀번호 해시 저장
                userRepository.save(user);
                tokenStore.remove(token);  // 사용한 토큰 제거
                return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
            }

            Partner partner = partnerRepository.findByPartnerEmail(email);
            if (partner != null) {
                partner.setPartnerPw(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
                partnerRepository.save(partner);
                tokenStore.remove(token);
                return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
            }
        }
        return ResponseEntity.status(400).body("유효하지 않은 요청입니다.");
    }

    private ResponseDto convertToDto(Object entity, String role) {
        ResponseDto responseDto = new ResponseDto();
        if (role.equals("USER")) {
            User user = (User) entity;
            responseDto.setId(user.getUserNum());
            responseDto.setEmail(user.getUserEmail());
            responseDto.setName(user.getUserName());
            responseDto.setNickname(user.getUserNickname());
            responseDto.setPhone(user.getUserPhone());
        } else if (role.equals("ADMIN")) {
            Admin admin = (Admin) entity;
            responseDto.setId(admin.getAdminNum());
            responseDto.setEmail(admin.getAdminEmail());
            responseDto.setName(admin.getAdminName());
        } else if (role.equals("PARTNER")) {
            Partner partner = (Partner) entity;
            responseDto.setId(partner.getPartnerNum());
            responseDto.setEmail(partner.getPartnerEmail());
            responseDto.setCompanyName(partner.getCompanyName());
            responseDto.setPhone(partner.getPartnerPhone());
        }
        return responseDto;
    }
}
