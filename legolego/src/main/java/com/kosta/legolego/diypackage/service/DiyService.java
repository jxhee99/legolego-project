package com.kosta.legolego.diypackage.service;

import com.kosta.legolego.diypackage.dto.*;
import com.kosta.legolego.diypackage.entity.DetailCourseEntity;
import com.kosta.legolego.diypackage.entity.DiyEntity;
import com.kosta.legolego.diypackage.entity.AirlineEntity;
import com.kosta.legolego.diypackage.entity.RouteEntity;
import com.kosta.legolego.diypackage.repository.DetailCourseRepository;
import com.kosta.legolego.diypackage.repository.DiyRepository;
import com.kosta.legolego.diypackage.repository.AirlineRepository;
import com.kosta.legolego.diypackage.repository.RouteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiyService {
  @Autowired
  private DiyRepository diyRepository;
  @Autowired
  private AirlineRepository airlineRepository;
  @Autowired
  private RouteRepository routeRepository;
  @Autowired
  private DetailCourseRepository detailCourseRepository;

  @Transactional
  public DiyEntity createDiy(RequestDTO requestDTO) {
    AirlineDTO airlineDTO = requestDTO.getAirline();
    RouteDTO routeDTO = requestDTO.getRoute();
    List<DetailCourseDTO> detailCourseDTOs = requestDTO.getDetailCourses();
    PackageFormDTO packageFormDTO = requestDTO.getPackageForm();

    AirlineEntity airlineEntity = airlineDTO.toEntity();
    RouteEntity routeEntity = routeDTO.toEntity();

    airlineEntity = airlineRepository.save(airlineEntity);
    routeEntity = routeRepository.save(routeEntity);

    //for문으로 detailCourseDTO 갯수 만큼 유동적으로 처리
    for (DetailCourseDTO detailCourseDTO : detailCourseDTOs) {
      // RouteEntity를 DetailCourseDTO에 설정
      detailCourseDTO.setRoute(routeEntity);
      DetailCourseEntity detailCourseEntity = detailCourseDTO.toEntity();
      detailCourseEntity = detailCourseRepository.save(detailCourseEntity);
    }

    //diyEntity 생성
    DiyEntity diyEntity = DiyEntity.builder()
            .packageName(packageFormDTO.getPackageName())
            .profileImg(packageFormDTO.getProfileImg())
            .shortDescription(packageFormDTO.getShortDescription())
            .regDate(LocalDate.now())
            .airline(airlineEntity)
            .route(routeEntity)
            .userNum(requestDTO.getUserNum()) // user부분 추후 수정
            .build();

    return diyRepository.save(diyEntity);
  }
}
