package com.kosta.legolego.diypackage.controller;

import com.kosta.legolego.diypackage.dto.*;
import com.kosta.legolego.diypackage.entity.DiyEntity;
import com.kosta.legolego.diypackage.service.DiyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/packages")
public class DiyController {
  @Autowired
  DiyService diyService;
  @PostMapping
  public ResponseEntity<Void> createDiy(@RequestBody RequestDTO requestDTO){
    DiyEntity diyEntity = diyService.createDiy(requestDTO);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
  @GetMapping("/{package_num}")
  public ResponseEntity<ResponseDTO> getDiyDetail(@PathVariable Long package_num){
    ResponseDTO responseDTO = diyService.getDiyDetail(package_num);
    return new ResponseEntity<>(responseDTO, HttpStatus.OK);
  }

  @PutMapping("/{package_num}")
  //추후 수정 권한 검사 추가
  public ResponseEntity<Void> updateDiy(@PathVariable Long package_num,@RequestBody RequestDTO requestDTO){
    DiyEntity diyEntity = diyService.updateDiy(package_num, requestDTO);
    return new ResponseEntity<>(HttpStatus.OK);
  }
  @DeleteMapping("/{package_num}")
  //추후 삭제 권한 검사 추가
  public ResponseEntity<Void> deleteDiy(@PathVariable Long package_num){
    diyService.deleteDiy(package_num);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
