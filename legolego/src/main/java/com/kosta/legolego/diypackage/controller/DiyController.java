package com.kosta.legolego.diypackage.controller;

import com.kosta.legolego.diypackage.dto.*;
import com.kosta.legolego.diypackage.entity.DiyEntity;
import com.kosta.legolego.diypackage.service.DiyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DiyController {
  @Autowired
  DiyService diyService;
  @PostMapping("/packages")
  public DiyEntity createDiy(@RequestBody RequestDTO requestDTO){

    return diyService.createDiy(requestDTO);
  }
  @GetMapping("/packages/{package_num}")
  public ResponseDTO getPackageDetail(@PathVariable Long package_num){
    return diyService.getDiyDetail(package_num);
  }

  @PutMapping("/packages/{package_num}")
  public DiyEntity updatedPackage(@PathVariable Long package_num,@RequestBody RequestDTO requestDTO){
    return diyService.updateDiy(package_num, requestDTO);
  }
}
