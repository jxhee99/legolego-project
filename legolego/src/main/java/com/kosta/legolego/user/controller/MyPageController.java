package com.kosta.legolego.user.controller;

import com.kosta.legolego.user.dto.MyPageDto;
import com.kosta.legolego.user.service.MyPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/myPage")
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    // 내가 쓴 글 목록 조회
//    @GetMapping("/packages/{user_num}")
//    public List<MyPageDto> getUserMyPage(@PathVariable("user_num") Long userNum) {
//        return myPageService.getUserMyPage(userNum);
//    }

    // 내가 쓴 글 목록 중 응원하기 조건 충족한 리스트만 조회
    @GetMapping("/liked_packages/{user_num}")
    public List<MyPageDto> getUserLikedPackages(@PathVariable("user_num") Long userNum,
                                                @RequestParam(value = "package_liked_num", defaultValue = "25") Integer packageLikedNum) {
        return myPageService.getUserLikedPackages(userNum, packageLikedNum);
    }

    // 여행사가 제안한 폼 확인
//    @GetMapping("/")
}