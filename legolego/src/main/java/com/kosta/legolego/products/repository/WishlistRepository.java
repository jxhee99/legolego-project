package com.kosta.legolego.products.repository;

import com.kosta.legolego.products.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUser_userNum(Long userNum);
    Optional<Wishlist> findByUser_userNumAndProduct_productNum(Long userNum, Long productNum);
}
