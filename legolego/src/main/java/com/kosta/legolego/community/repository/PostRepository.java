package com.kosta.legolego.community.repository;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.community.entity.Post;
import com.kosta.legolego.community.entity.Post.PostCategory;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 카테고리별 게시글 조회
    List<Post> findByPostCategory(PostCategory category);

    // 내가 쓴 글 목록 조회
    List<Post> findByUser(User user);
    List<Post> findByPartner(Partner partner);
    List<Post> findByAdmin(Admin admin);

    // 내가 댓글 단 글 목록 조회
    List<Post> findByCommentsUser(User user);
    List<Post> findByCommentsPartner(Partner partner);
    List<Post> findByCommentsAdmin(Admin admin);
}
