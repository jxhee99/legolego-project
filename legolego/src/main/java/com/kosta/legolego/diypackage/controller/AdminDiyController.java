package com.kosta.legolego.diypackage.controller;

import com.kosta.legolego.diypackage.service.DiyService;
import com.kosta.legolego.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/packages")
public class AdminDiyController {
  @Autowired
  DiyService diyService;
  @DeleteMapping("/{package_num}")
  public ResponseEntity<Void> deleteDiy(@PathVariable("package_num") Long package_num, @AuthenticationPrincipal CustomUserDetails userDetails) {

    if (userDetails == null || !userDetails.getRole().equals("ROLE_ADMIN")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    diyService.deleteDiy(package_num);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
