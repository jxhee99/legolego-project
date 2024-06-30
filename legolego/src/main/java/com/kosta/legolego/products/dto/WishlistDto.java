package com.kosta.legolego.products.dto;

//import com.kosta.legolego.products.entity.Wishlist;
import com.kosta.legolego.products.entity.Wishlist;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class WishlistDto {
    private Long wishlistNum;
    private Long userNum;
    private Long productNum;
    private boolean wishlistStatus;

// wishlist 엔티티 -> wishlist DTO로 변환하는 생성자
    public WishlistDto(Wishlist wishlist){
        this.wishlistNum = wishlist.getWishlist_num();
        this.userNum = wishlist.getUser().getUserNum();
        this.productNum = wishlist.getProduct().getProductNum();
        this.wishlistStatus = isWishlistStatus();
    }


}
