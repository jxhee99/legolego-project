package com.kosta.legolego.products.service;

import com.kosta.legolego.products.dto.WishlistDto;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.entity.Wishlist;
import com.kosta.legolego.products.repository.ProductRepository;
import com.kosta.legolego.products.repository.WishlistRepository;
import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    WishlistRepository wishlistRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    // 상품 찜 하기
    public void addToWishlist(Long userNum, Long productNum){
        // 1. 사용자와 상품을 엔티티로 조회
        User user = userRepository.findById(userNum)
                .orElseThrow(()-> new RuntimeException("상품을 찾을 수 없습니다"));;
        Product product = productRepository.findById(productNum)
                .orElseThrow(()-> new RuntimeException("상품을 찾을 수 없습니다"));

        // 2. 사용자 중복 찜 불가능(패키지 하나 - 유저 한 명)
        Optional<Wishlist> existingWish = wishlistRepository.findByUser_userNumAndProduct_productNum(userNum, productNum);
        if(existingWish.isPresent()) {
            throw new RuntimeException("이미 찜한 상품입니다.");
        }

        // 3. 찜하기 엔티티 생성 후 저장
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);
        wishlist.setWishlistStatus(true);
        wishlistRepository.save(wishlist);

        // 4. 상품 테이블의 wishlist count 필드 업데이트
        product.setWishlistCount(product.getWishlistCount() +1);
        productRepository.save(product);
    }

    // 찜 목록 조회
    public List<WishlistDto> getWishlistByUser(Long userNum){
        // 사용자 별로 찜 목록을 반환
        return wishlistRepository.findByUser_userNum(userNum)
                .stream()
                .map(WishlistDto::new) // wishlist 엔티티 -> wishlist DTO로 변환
                .collect(Collectors.toList());
    }

    //  찜 목록에서 상품 제거
    public void  removeFromWishlist(Long userNum, Long productNum){
        Wishlist wishlist = wishlistRepository.findByUser_userNumAndProduct_productNum(userNum, productNum)
                .orElseThrow(() -> new ResolutionException("찜 목록에서 상품을 찾을 수 없습니다."));
        wishlistRepository.delete(wishlist);
        wishlist.setWishlistStatus(false);
        wishlistRepository.save(wishlist);

        // 상품 테이블의 wishlist count 필드 업데이트
        Product product = productRepository.findById(productNum)
                .orElseThrow(()->new RuntimeException("찜 목록에서 상품을 찾을 수 없습니다."));
        product.setWishlistCount(product.getWishlistCount() -1);
        productRepository.save(product);
    }

}
