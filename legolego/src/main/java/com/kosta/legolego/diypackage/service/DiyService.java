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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

  public ResponseDTO getPackageDetail(Long packageNum){

    DiyEntity diyEntity = diyRepository.findById(packageNum) .orElse(null);
    AirlineEntity airlineEntity = diyEntity.getAirline();
    RouteEntity routeEntity = diyEntity.getRoute();
    // DetailCourseEntity 리스트 조회
    List<DetailCourseEntity> detailCourseEntities = detailCourseRepository.findByRoute(routeEntity);
    // AirlineDTO 설정
    AirlineDTO airlineDTO = AirlineDTO.builder()
            .airlineNum(airlineEntity.getAirlineNum())
            .airlineName(airlineEntity.getAirlineName())
            .startingPoint(airlineEntity.getStartingPoint())
            .destination(airlineEntity.getDestination())
            .startFlightNum(airlineEntity.getStartFlightNum())
            .boardingDate(airlineEntity.getBoardingDate())
            .comeFlightNum(airlineEntity.getComeFlightNum())
            .comingDate(airlineEntity.getComingDate())
            .build();

    // RouteDTO 설정
    RouteDTO routeDTO = RouteDTO.builder()
            .routeNum((routeEntity.getRouteNum()))
            .startDate(routeEntity.getStartDate())
            .lastDate(routeEntity.getLastDate())
            .build();

    // DetailCourseDTO 설정
    List<DetailCourseDTO> detailCourseDTOList = detailCourseEntities.stream()
            .map(detailCourse -> DetailCourseDTO.builder()
                    .detailCourseNum(detailCourse.getDetailCourseNum())
                    .dayNum(detailCourse.getDayNum())
                    .route(detailCourse.getRoute())
                    .courses(Arrays.asList(
                            detailCourse.getCourse1(),
                            detailCourse.getCourse2(),
                            detailCourse.getCourse3(),
                            detailCourse.getCourse4(),
                            detailCourse.getCourse5(),
                            detailCourse.getCourse6(),
                            detailCourse.getCourse7(),
                            detailCourse.getCourse8(),
                            detailCourse.getCourse9(),
                            detailCourse.getCourse10()
                    ).stream().filter(Objects::nonNull).collect(Collectors.toList()))
                    .fileUrl(detailCourse.getFileUrl())
                    .build())
            .collect(Collectors.toList());

    // PackageFormDTO설정
    PackageFormDTO packageFormDTO = PackageFormDTO.getDiyEntity(diyEntity);

    //임시 userNum
    int userNum = diyEntity.getUserNum();

    // ResponseDTO 설정
    ResponseDTO responseDTO = ResponseDTO.builder()
            .airline(airlineDTO)
            .route(routeDTO)
            .packageForm(packageFormDTO)
            .detailCourses(detailCourseDTOList)
            .userNum(userNum)
            .build();

    return responseDTO;
  }
}
