package com.kosta.legolego.diypackage.controller;


import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.service.DiyListService;
import com.kosta.legolego.security.CustomUserDetails;
import com.kosta.legolego.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@RestController
//@RequestMapping("/diylists")
public class DiyListController {

    @Autowired
    DiyListService diyListService;

    @GetMapping("/admin/diylists")
    public ResponseEntity<List<DiyList>> getDiyListForAdmin(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || !userDetails.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<DiyList> diyLists = diyListService.getAllDiyListsForAdmin();
        return ResponseEntity.ok(diyLists);
    }

    @GetMapping("/partner/diylists")
    public ResponseEntity<List<DiyList>> getDiyListsForPartner(@AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long partnerNum = userDetails.getId();
        List<DiyList> diyListsForPartner = diyListService.getDiyListsForPartner(partnerNum);
        return ResponseEntity.ok(diyListsForPartner);
    }

    @GetMapping("/user/diylists")
    public ResponseEntity<List<DiyList>> getDiyListsForUser(@AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userNum = userDetails.getId();
        List<DiyList> diyListsForUser = diyListService.getDiyListsForUser(userNum);
        return ResponseEntity.ok(diyListsForUser);
    }

    // jwt 토큰으로 사용자 인증
//    public ResponseEntity<List<DiyList>> getDiyListsForUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser){
//        Long userId = Long.parseLong(currentUser.getUsername());  // JWT 토큰에서 가져온 사용자 ID
//        List<DiyList> diyListsForUser = diyListService.getDiyListsForUser(userId);
//        return ResponseEntity.ok(diyListsForUser);
//    }

    // 가격 제안 수락
    @PostMapping("/user/accept")
    public ResponseEntity<DiyList> acceptProposal(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("list_num") Long listNum, @RequestParam("package_num") Long packageNum) {
        if (userDetails == null || !userDetails.getRole().equals("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        DiyList acceptedProposal = diyListService.acceptProposal(listNum, packageNum);
        log.info("list_num : {}, package_num : {}", listNum, packageNum);
        return ResponseEntity.ok(acceptedProposal);
    }

    // 상품 등록 승인
    @PostMapping("/admin/register")
    public ResponseEntity<DiyList> registerProduct(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("list_num") Long listNum, @RequestParam("recruitment_dead_line") String recruitmentDeadline){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long adminNum = userDetails.getId();
        DiyList registeredProduct = diyListService.registerProduct(adminNum, listNum, recruitmentDeadline);
        return ResponseEntity.ok(registeredProduct);
    }


}
