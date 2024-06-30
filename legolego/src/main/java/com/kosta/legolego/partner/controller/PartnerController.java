package com.kosta.legolego.partner.controller;

import com.kosta.legolego.partner.dto.PartnerProfileDto;
import com.kosta.legolego.partner.service.PartnerService;
import com.kosta.legolego.security.CustomUserDetails;
import com.kosta.legolego.user.dto.UpdatePasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/partner")
public class PartnerController {

    @Autowired
    private PartnerService partnerService;

    // 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<PartnerProfileDto> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || !"ROLE_PARTNER".equals(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PartnerProfileDto profile = partnerService.getProfile(userDetails.getId());
        return ResponseEntity.ok(profile);
    }

    // 프로필 변경
    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestBody PartnerProfileDto request) {
        if (userDetails == null || !"ROLE_PARTNER".equals(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isUpdated = partnerService.updateProfile(userDetails.getId(), request);
        if (isUpdated) {
            return ResponseEntity.ok("전화번호가 성공적으로 변경되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("업데이트 중 오류가 발생했습니다.");
        }
    }

    // 비밀번호 변경
    @PatchMapping("/profile/password")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @RequestBody UpdatePasswordDto request) {
        if (userDetails == null || !"ROLE_PARTNER".equals(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            boolean isUpdated = partnerService.updatePassword(userDetails.getId(), request);
            if (isUpdated) {
                return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 탈퇴
    @DeleteMapping("/profile/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || !"ROLE_PARTNER".equals(userDetails.getRole())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        partnerService.deleteUser(userDetails.getId());
        return ResponseEntity.ok("탈퇴가 완료되었습니다.");
    }

}
