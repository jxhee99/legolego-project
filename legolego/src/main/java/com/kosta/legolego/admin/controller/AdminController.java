package com.kosta.legolego.admin.controller;

import com.kosta.legolego.admin.dto.AdminProfileDto;
import com.kosta.legolego.admin.dto.MemberDto;
import com.kosta.legolego.admin.service.AdminService;
import com.kosta.legolego.security.CustomUserDetails;
import com.kosta.legolego.user.dto.UpdatePasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<AdminProfileDto> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || !"ROLE_ADMIN".equals(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AdminProfileDto profile = adminService.getProfile(userDetails.getId());
        return ResponseEntity.ok(profile);
    }

    // 프로필 변경
    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestBody AdminProfileDto request) {
        if (userDetails == null || !"ROLE_ADMIN".equals(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isUpdated = adminService.updateProfile(userDetails.getId(), request);
        if (isUpdated) {
            return ResponseEntity.ok("이름이 성공적으로 변경되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("업데이트 중 오류가 발생했습니다.");
        }
    }

    // 비밀번호 변경
    @PatchMapping("/profile/password")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @RequestBody UpdatePasswordDto request) {
        if (userDetails == null || !"ROLE_ADMIN".equals(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            boolean isUpdated = adminService.updatePassword(userDetails.getId(), request);
            if (isUpdated) {
                return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 전체 회원 조회
    @GetMapping("/members")
    public ResponseEntity<List<MemberDto>> getAllMembers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || !"ROLE_ADMIN".equals(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<MemberDto> members = adminService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    // 프로필 이미지 업데이트
    @PatchMapping("/profile/image")
    public ResponseEntity<String> updateProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @RequestParam("image") MultipartFile image) {
        if (userDetails == null || !"ROLE_ADMIN".equals(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            adminService.updateProfileImage(userDetails.getId(), image);
            return ResponseEntity.ok("프로필 이미지 변경이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이미지 업로드 중 오류가 발생했습니다.");
        }
    }

    // 프로필 이미지 조회 (기본 이미지 처리)
    @GetMapping("/profile/image")
    public ResponseEntity<String> getProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || !"ROLE_ADMIN".equals(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String profileImage = adminService.getProfileImage(userDetails.getId());
        return ResponseEntity.ok(profileImage);
    }
}
