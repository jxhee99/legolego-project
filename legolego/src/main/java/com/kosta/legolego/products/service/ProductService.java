package com.kosta.legolego.products.service;

import com.kosta.legolego.admin.repository.AdminRepository;
import com.kosta.legolego.diypackage.dto.DiyAirlineDTO;
import com.kosta.legolego.diypackage.dto.DiyDetailCourseDTO;
import com.kosta.legolego.diypackage.dto.DiyRouteDTO;
import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.repository.DetailCourseRepository;
import com.kosta.legolego.diypackage.repository.DiyListRepository;
import com.kosta.legolego.products.dto.ProductDetailDto;
import com.kosta.legolego.products.dto.ProductDetailInfo;
import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    DiyListRepository diyListRepository;

    @Autowired
    DetailCourseRepository detailCourseRepository;

    @Autowired
    AdminRepository adminRepository;


//  상품 전체 조회
    public List<ProductDto> getAllProducts(){
        return productRepository.findAll().stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    //   상품 상세 조회
    public ProductDetailDto getProductByDetailId(Long productNum){
        Product product = productRepository.findById(productNum)
                .orElseThrow(()->new RuntimeException("상품을 찾을 수 없습니다."));

        DiyList diyList = diyListRepository.findByProductNum(productNum)
                .orElseThrow(()->new RuntimeException("Diy 리스트를 찾을 수 없습니다."));

        DiyAirlineDTO diyAirlineDTO = DiyAirlineDTO.toAirlineDTO(diyList.getDiyPackage().getAirline());
        DiyRouteDTO diyRouteDTO = DiyRouteDTO.toRouteDTO(diyList.getDiyPackage().getRoute());
        List<DiyDetailCourseDTO> diyDetailCourseDTOList = DiyDetailCourseDTO
                .toDetailCourseDTOList(detailCourseRepository
                        .findByRoute(diyList.getDiyPackage().getRoute()));

        ProductDetailInfo info = new ProductDetailInfo(diyList, diyAirlineDTO, diyRouteDTO, diyDetailCourseDTOList);

        return ProductDetailDto.fromInfo(product, info);

    }

//    상품 검색
    public List<ProductDto> searchProducts(String keyword){
        return productRepository.findByProductNameContaining(keyword).stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

//    관리자용 상품 전체 조회
public List<ProductDto> getAllProductsForAdmin(){
        return productRepository.findAll().stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
}

//   only admin - 상품 수정
public ProductDto updateProduct(Long productNum, ProductDto productDto, Long adminNum){
    // 관리자인지 확인
    if(!adminRepository.existsById(adminNum)) {
//        throw new IllegalArgumentException("상품을 수정 할 수 있는 권한이 없습니다.");
        throw new IllegalArgumentException("상품을 수정 할 수 있는 권한이 없습니다.");
    }

    Product product = productRepository.findById(productNum)
            .orElseThrow(()-> new IllegalArgumentException("상품 수정 실패! " + "대상 상품이 없습니다."));
    product.patch(productDto);

    Product updatedProduct = productRepository.save(product);

    return ProductDto.fromEntity(updatedProduct);
}

//    only admin - 상품 삭제
    public void deleteProduct(Long productNum, Long adminNum){
        if(!adminRepository.existsById(adminNum)){
            throw new IllegalArgumentException("상품을 삭제 할 수 있는 권한이 없습니다.");
        }

      if(!productRepository.existsById(productNum)){
          throw new IllegalArgumentException("상품 삭제 실패! 대상 상품이 없습니다.");
      }
      productRepository.deleteById(productNum);
    }

}