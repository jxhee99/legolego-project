package com.kosta.legolego.partner.controller;

import com.kosta.legolego.partner.dto.OfferFormDto;
import com.kosta.legolego.diypackage.entity.OverLikedList;
import com.kosta.legolego.partner.dto.PartnerProductDto;
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
  public ResponseEntity<?> getOverLikedList(@AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      List<OverLikedList> overLikedLists = partnerPackageService.getOverLikedList(userDetails.getId());
      return ResponseEntity.ok(overLikedLists);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to retrieve over-liked packages", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // 작성자에게 가격 제안하기
  @PostMapping("/over-liked-packages/offer")
  public ResponseEntity<?> submitOfferForm(@RequestBody OfferFormDto offerFormDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      if (userDetails == null || !userDetails.getRole().equals("ROLE_PARTNER")) {
        return new ResponseEntity<>("Partner not authenticated", HttpStatus.UNAUTHORIZED);
      }
      partnerPackageService.submitOfferForm(offerFormDto, userDetails.getId());
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to submit offer form", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  //여행상품, 주문내역 조회
  @GetMapping("/products")
  public ResponseEntity<?> getProducts(@AuthenticationPrincipal CustomUserDetails userDetails){
    try {
      if (userDetails == null || !userDetails.getRole().equals("ROLE_PARTNER")) {
        return new ResponseEntity<>("Partner not authenticated", HttpStatus.UNAUTHORIZED);
      }
      List<PartnerProductDto> products = partnerPackageService.getProductOrders(userDetails.getId());
      return ResponseEntity.ok(products);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to get products", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
