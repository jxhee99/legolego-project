package com.kosta.legolego.products.service;

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


//  상품 전체 조회
    public List<ProductDto> getAllProducts(){
        return productRepository.findAll().stream()
                .map(ProductDto::createProductDto)
                .collect(Collectors.toList());
    }

    //   상품 상세 조회
    public ProductDto getProductById(Long productNum){
        Product product = productRepository.findById(productNum)
                .orElseThrow(()-> new RuntimeException("상품을 찾을 수 없습니다."));
        return ProductDto.createProductDto(product);
    }

//    상품 검색
    public List<ProductDto> searchProducts(String keyword){
        return productRepository.findByProductNameContaining(keyword).stream()
                .map(ProductDto::createProductDto)
                .collect(Collectors.toList());
    }

//    관리자용 상품 전체 조회
public List<ProductDto> getAllProductsForAdmin(){
        return productRepository.findAll().stream()
                .map(ProductDto::createProductDto)
                .collect(Collectors.toList());
}

//    관리자 상품 수정
    public ProductDto updateProduct(Long productNum, ProductDto productDto){
        Product product = productRepository.findById(productNum)
                .orElseThrow(()-> new IllegalArgumentException("상품 수정 실패! " + "대상 상품이 없습니다."));
        product.patch(productDto);

        Product updatedProduct = productRepository.save(product);

        return ProductDto.createProductDto(updatedProduct);
    }

//    관리자 상품 삭제
    public void deleteProduct(Long productNum){
      if(!productRepository.existsById(productNum)){
          throw new IllegalArgumentException("상품 삭제 실패! 대상 상품이 없습니다.");
      }
      productRepository.deleteById(productNum);
    }

}