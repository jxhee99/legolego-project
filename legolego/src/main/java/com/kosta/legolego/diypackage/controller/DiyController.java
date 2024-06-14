package com.kosta.legolego.diypackage.controller;

import com.kosta.legolego.diypackage.dto.*;
import com.kosta.legolego.diypackage.entity.DiyEntity;
import com.kosta.legolego.diypackage.service.DiyService;
import com.kosta.legolego.diypackage.service.DiyLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packages")
public class DiyController {
  @Autowired
  DiyService diyService;
  @Autowired
  DiyLikeService diyLikeService;

  @PostMapping
  public ResponseEntity<Void> createDiy(@RequestBody RequestDTO requestDTO) {
    DiyEntity diyEntity = diyService.createDiy(requestDTO);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
  //전체 조회
  @GetMapping
  public ResponseEntity<List<DiyEntity>> getDiyPackages() {
    List<DiyEntity> diyPackages = diyService.getDiyPackages();
    return ResponseEntity.ok(diyPackages);
  }

  //상세조회
  @GetMapping("/{package_num}")
  public ResponseEntity<ResponseDTO> getDiyDetail(@PathVariable Long package_num) {
    //로그인한 사용자 임시 하드코딩
    Long currentUserNum = 3L;
    ResponseDTO responseDTO = diyService.getDiyDetail(package_num, currentUserNum);
    return new ResponseEntity<>(responseDTO, HttpStatus.OK);
  }
  //put방식
  @PutMapping("/{package_num}")
  //추후 수정 권한 검사 추가
  public ResponseEntity<Void> updateDiy(@PathVariable Long package_num, @RequestBody RequestDTO requestDTO) {
    DiyEntity diyEntity = diyService.updateDiy(package_num, requestDTO);
    return new ResponseEntity<>(HttpStatus.OK);
  }
  //patch방식
  @PatchMapping("/{package_num}")
  //추후 수정 권한 검사 추가
  public ResponseEntity<Void> updateDiyPatch(@PathVariable Long package_num, @RequestBody RequestDTO requestDTO) {
    DiyEntity diyEntity = diyService.updateDiyPatch(package_num, requestDTO);
    return new ResponseEntity<>(HttpStatus.OK);
  }
  @DeleteMapping("/{package_num}")
  //추후 삭제 권한 검사 추가
  public ResponseEntity<Void> deleteDiy(@PathVariable Long package_num) {
    diyService.deleteDiy(package_num);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  //가수요 참여
  @PostMapping("/likes/{package_num}")
  public ResponseEntity<?> likeDiy(@PathVariable Long package_num, @RequestBody DiyLikeDTO diyLikeDTO) {
    //로그인한 사용자인지 등 userNum에 관한 처리 추후에 추가
    try {
      diyLikeDTO.setPackageNum(package_num);
      diyLikeService.diyLike(diyLikeDTO);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
  @DeleteMapping("/likes/{package_num}")
  public ResponseEntity<?> likeCancel(@PathVariable Long package_num, @RequestBody DiyLikeDTO diyLikeDTO) {
    try {
      diyLikeDTO.setPackageNum(package_num);
      diyLikeService.likeCancel(diyLikeDTO);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}