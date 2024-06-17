package com.kosta.legolego.admin.controller;

import com.kosta.legolego.admin.dto.AdminLikedListDto;
import com.kosta.legolego.admin.service.AdminLikedListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminLikedListController {

    @Autowired
    private AdminLikedListService adminLikedListService;

    // 응원하기 조건 충족한 DIY 패키지 리스트 조회
    @GetMapping("/admin/liked_packages")
    public List<AdminLikedListDto> getLikedList(@RequestParam(value = "package_liked_num", defaultValue = "25") Integer packageLikedNum) {
        return adminLikedListService.getLikedList(packageLikedNum);
    }

    // DIY 패키지 승인
    @PatchMapping("/admin/liked_packages/{list_num}/approve")
    public AdminLikedListDto approvePackage(@PathVariable("list_num") Long listNum) {
        return adminLikedListService.approvePackage(listNum);
    }

    // DIY 패키지 거절
    @PatchMapping("/admin/liked_packages/{list_num}/reject")
    public AdminLikedListDto rejectPackage(@PathVariable("list_num") Long listNum) {
        return adminLikedListService.rejectPackage(listNum);
    }
}
