package com.kosta.legolego.community.repository;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.community.entity.Post;
import com.kosta.legolego.community.entity.Post.PostCategory;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.user.entity.User;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long> {

    // 내가 쓴 글 목록 조회
    List<Post> findByUser(User user);
    List<Post> findByPartner(Partner partner);
    List<Post> findByAdmin(Admin admin);

    // 내가 댓글 단 글 목록 조회
    List<Post> findByCommentsUser(User user);
    List<Post> findByCommentsPartner(Partner partner);
    List<Post> findByCommentsAdmin(Admin admin);

    // 검색
    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    List<Post> searchPostsByKeyword(@Param("keyword") String keyword);

    // 모든 게시글 최신순 정렬
    List<Post> findAllByOrderByRegDateDesc();

    // 모든 게시글 오래된 순으로 정렬
    List<Post> findAllByOrderByRegDateAsc();

    // 카테고리별 게시글 정렬
    //List<Post> findByPostCategoryOrderByRegDateDesc(PostCategory category);
    //List<Post> findByPostCategoryOrderByRegDateAsc(PostCategory category);
    List<Post> findByPostCategoryOrderByPostNumDesc(PostCategory category);
    List<Post> findByPostCategoryOrderByPostNumAsc(PostCategory category);

    // 카테고리 내에서 키워드 검색
    @Query("SELECT p FROM Post p WHERE p.postCategory = :category AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    List<Post> findByPostCategoryAndKeyword(@Param("category") PostCategory category, @Param("keyword") String keyword);

    //등록 시간 없어서 최신순, 오래된 순 PostNum기준으로 반환
    List<Post> findAllByOrderByPostNumDesc();

    List<Post> findAllByOrderByPostNumAsc();
}
