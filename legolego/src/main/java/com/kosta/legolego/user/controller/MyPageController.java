package com.kosta.legolego.user.controller;

import com.kosta.legolego.user.dto.MyPageDto;
import com.kosta.legolego.user.service.MyPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/my")
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    // 내가 쓴 글 리스트 조회
    @GetMapping("/packages/{user_num}")
    public List<MyPageDto> getMyPackages(@PathVariable("user_num") Long userNum) {
        // 추후 JWT 토큰에서 인증된 사용자 정보 추출
        return myPageService.getPackagesByUserNum(userNum);
    }

    // 응원하기 버튼 누른 게시물 리스트 조회
    @GetMapping("/likes/{user_num}")
    public List<MyPageDto> getLikedPackages(@PathVariable("user_num") Long userNum) {
        return myPageService.getLikedPackagesByUserNum(userNum);
    }

    // 내가 쓴 글 목록 중 응원하기 조건 충족한 리스트만 조회
//    @GetMapping("/liked_packages/{user_num}")
//    public List<MyPageDto> getUserLikedPackages(@PathVariable("user_num") Long userNum,
//                                                @RequestParam(value = "package_liked_num", defaultValue = "25") Integer packageLikedNum) {
//        return myPageService.getUserLikedPackages(userNum, packageLikedNum);
//    }


}