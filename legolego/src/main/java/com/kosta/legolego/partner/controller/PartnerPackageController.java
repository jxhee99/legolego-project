package com.kosta.legolego.partner.controller;

import com.kosta.legolego.diypackage.entity.OverLikedList;
import com.kosta.legolego.partner.service.PartnerPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
