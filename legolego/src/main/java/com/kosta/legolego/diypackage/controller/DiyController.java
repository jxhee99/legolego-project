package com.kosta.legolego.diypackage.controller;

import com.kosta.legolego.diypackage.dto.*;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.service.DiyService;
import com.kosta.legolego.diypackage.service.DiyLikeService;
import com.kosta.legolego.security.CustomUserDetails;
import com.kosta.legolego.security.CustomUserDetailsService;
import com.kosta.legolego.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class DiyController {
  @Autowired
  DiyService diyService;
  @Autowired
  DiyLikeService diyLikeService;

  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  @PostMapping("/user/packages")
  public ResponseEntity<Long> createDiy(@RequestBody RequestDTO requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long userNum = userDetails.getUserNum();
    try {
      requestDTO.setUserNum(userNum);
      Long packageNum = diyService.createDiy(requestDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(packageNum);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(null); // 예외 처리
    }
  }

  //전체 조회
  @GetMapping("/packages")
  public ResponseEntity<List<DiyPackage>> getDiyPackages() {
    List<DiyPackage> diyPackages = diyService.getDiyPackages();
    return ResponseEntity.ok(diyPackages);
  }

  //상세조회
  @GetMapping("/packages/{package_num}")
  public ResponseEntity<?> getDiyDetail(@PathVariable("package_num") Long package_num) {
    try {
      //로그인한 사용자 임시 하드코딩
      Long currentUserNum = 3L;
      ResponseDTO responseDTO = diyService.getDiyDetail(package_num, currentUserNum);
      return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    catch (RuntimeException e){
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  //put방식 (이미지 추가 후 고장남)
  @PutMapping("/user/packages/{package_num}")
  //추후 수정 권한 검사 추가
  public ResponseEntity<?> updateDiy(@PathVariable("package_num") Long package_num, @RequestBody RequestDTO requestDTO) {
    try{
      DiyPackage diyPackage = diyService.updateDiy(package_num, requestDTO);
      return new ResponseEntity<>(HttpStatus.OK);
    }catch (RuntimeException e){
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
  //patch방식 (이미지 추가 후 고장남)
  @PatchMapping("/user/packages/{package_num}")
  //추후 수정 권한 검사 추가
  public ResponseEntity<?> updateDiyPatch(@PathVariable("package_num") Long package_num, @RequestBody RequestDTO requestDTO) {
    try{
      DiyPackage diyPackage = diyService.updateDiyPatch(package_num, requestDTO);
      return new ResponseEntity<>(HttpStatus.OK);
    }catch (RuntimeException e){
      return ResponseEntity.badRequest().body(e.getMessage());
    }

  }
  @DeleteMapping("/user/packages/{package_num}")
  //추후 삭제 권한 검사 추가
  public ResponseEntity<?> deleteDiy(@PathVariable("package_num") Long package_num) {
    try{
      diyService.deleteDiy(package_num);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }catch (RuntimeException e){
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  //가수요 참여
  @PostMapping("/user/packages/likes/{package_num}")
  public ResponseEntity<?> likeDiy(@PathVariable("package_num") Long package_num, @RequestBody DiyLikeDTO diyLikeDTO) {
    //로그인한 사용자인지 등 userNum에 관한 처리 추후에 추가
    try {
      diyLikeDTO.setPackageNum(package_num);
      diyLikeService.diyLike(diyLikeDTO);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
  @DeleteMapping("/user/packages/likes/{package_num}")
  public ResponseEntity<?> likeCancel(@PathVariable("package_num") Long package_num, @RequestBody DiyLikeDTO diyLikeDTO) {
    try {
      diyLikeDTO.setPackageNum(package_num);
      diyLikeService.likeCancel(diyLikeDTO);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}