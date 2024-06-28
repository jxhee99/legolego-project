package com.kosta.legolego.partner.controller;

import com.kosta.legolego.diypackage.dto.RequestDTO;
import com.kosta.legolego.partner.dto.OfferFormDto;
import com.kosta.legolego.diypackage.entity.OverLikedList;
import com.kosta.legolego.partner.service.PartnerPackageService;
import com.kosta.legolego.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partner")
public class PartnerPackageController {

  @Autowired
  PartnerPackageService partnerPackageService;
  @GetMapping("/over-liked-packages")
  public ResponseEntity<List<OverLikedList>> getOverLikedList(@AuthenticationPrincipal CustomUserDetails userDetails)  {
    List<OverLikedList> overLikedLists = partnerPackageService.getOverLikedList(userDetails.getId());
    return ResponseEntity.ok(overLikedLists);
  }
  // 작성자에게 가격 제안하기
  @PostMapping("/over-liked-packages/offer")
  public ResponseEntity<?>  submitOfferForm(@RequestBody OfferFormDto offerFormDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
    partnerPackageService.submitOfferForm(offerFormDto, userDetails.getId());
    return new ResponseEntity<>(HttpStatus.CREATED);
  }


}
