package com.kosta.legolego.products.dto;

import com.kosta.legolego.diypackage.dto.DiyAirlineDTO;
import com.kosta.legolego.diypackage.dto.DiyDetailCourseDTO;
import com.kosta.legolego.diypackage.dto.DiyRouteDTO;
import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.products.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductDetailInfo {
    private DiyList diyList;
    private DiyAirlineDTO diyAirlineDTO;
    private DiyRouteDTO diyRouteDTO;
    private List<DiyDetailCourseDTO> diyDetailCourseDTOList;
}
