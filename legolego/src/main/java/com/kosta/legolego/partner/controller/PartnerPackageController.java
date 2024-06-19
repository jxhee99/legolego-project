package com.kosta.legolego.partner.controller;

import com.kosta.legolego.diypackage.dto.RequestDTO;
import com.kosta.legolego.partner.dto.OfferFormDto;
import com.kosta.legolego.diypackage.entity.OverLikedList;
import com.kosta.legolego.partner.service.PartnerPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partner")
public class PartnerPackageController {

  @Autowired
  PartnerPackageService partnerPackageService;
  @GetMapping("/over-liked-packages/{partner_num}")
  public ResponseEntity<List<OverLikedList>> getOverLikedList(@PathVariable("partner_num") Long partner_num)  {
    List<OverLikedList> overLikedLists = partnerPackageService.getOverLikedList(partner_num);
    return ResponseEntity.ok(overLikedLists);
  }
  // 작성자에게 가격 제안하기
  @PostMapping("/over-liked-packages/{partner_num}/offer")
  public ResponseEntity<?>  submitOfferForm(@PathVariable("partner_num") Long partner_num ,@RequestBody OfferFormDto offerFormDto) {
    partnerPackageService.submitOfferForm(offerFormDto, partner_num);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }


}
