package com.kosta.legolego.community.repository;

import com.kosta.legolego.community.entity.Comment;
import com.kosta.legolego.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostAndParentCommentIsNull(Post post);
    List<Comment> findByParentComment(Comment parentComment);
}
