package com.kosta.legolego.user.controller;

import com.kosta.legolego.security.CustomUserDetails;
import com.kosta.legolego.user.dto.MyPageDto;
import com.kosta.legolego.user.dto.MyProfileDto;
import com.kosta.legolego.user.dto.UpdatePasswordDto;
import com.kosta.legolego.user.service.MyPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/my")
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    // 내가 쓴 글 리스트 조회
    @GetMapping("/packages")
    public List<MyPageDto> getMyPackages(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("사용자 인증이 필요합니다.");
        }
        Long userNum = userDetails.getId();
        return myPageService.getPackagesByUserNum(userNum);
    }

    // 응원하기 버튼 누른 게시물 리스트 조회
    @GetMapping("/likes")
    public List<MyPageDto> getLikedPackages(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("사용자 인증이 필요합니다.");
        }
        Long userNum = userDetails.getId();
        return myPageService.getLikedPackagesByUserNum(userNum);
    }


    // 내가 쓴 글 목록 중 응원하기 조건 충족한 리스트만 조회
//    @GetMapping("/liked_packages/{user_num}")
//    public List<MyPageDto> getUserLikedPackages(@PathVariable("user_num") Long userNum,
//                                                @RequestParam(value = "package_liked_num", defaultValue = "25") Integer packageLikedNum) {
//        return myPageService.getUserLikedPackages(userNum, packageLikedNum);
//    }

    // 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<MyProfileDto> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        MyProfileDto profile = myPageService.getProfile(userDetails.getId());
        return ResponseEntity.ok(profile);
    }

    // 프로필 변경
    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestBody MyProfileDto request) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isUpdated = myPageService.updateProfile(userDetails.getId(), request);
        if (isUpdated) {
            return ResponseEntity.ok("프로필이 성공적으로 변경되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("업데이트 중 오류가 발생했습니다.");
        }
    }

    // 비밀번호 변경
    @PatchMapping("/profile/password")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @RequestBody UpdatePasswordDto request) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            boolean isUpdated = myPageService.updatePassword(userDetails.getId(), request);
            if (isUpdated) {
                return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원 탈퇴
    @DeleteMapping("/profile/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        myPageService.deleteUser(userDetails.getId());
        return ResponseEntity.ok("탈퇴가 완료되었습니다.");
    }

}