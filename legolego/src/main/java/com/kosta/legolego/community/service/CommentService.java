package com.kosta.legolego.community.service;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.admin.repository.AdminRepository;
import com.kosta.legolego.community.dto.CommentDto;
import com.kosta.legolego.community.entity.Comment;
import com.kosta.legolego.community.entity.Post;
import com.kosta.legolego.community.repository.CommentRepository;
import com.kosta.legolego.community.repository.PostRepository;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.partner.repository.PartnerRepository;
import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private AdminRepository adminRepository;

    // 전체 댓글 조회
    public List<CommentDto> getCommentsByPost(Long postNum) {
        Post post = postRepository.findById(postNum)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        List<Comment> topLevelComments = commentRepository. findByPostAndParentCommentIsNull(post);
        return topLevelComments.stream()
                .map(this::convertToDtoWithReplies)
                .filter(commentDto -> !(commentDto.getDeleted() && commentDto.getReplies().isEmpty()))  // 대댓글이 없는 삭제된 댓글은 제외
                .collect(Collectors.toList());
    }

    // 댓글 작성
    public CommentDto createComment(CommentDto commentDto) {
        Comment comment = convertToEntity(commentDto);
        comment.setRegDate(LocalDate.now());
        comment.setDeleted(false);
        Comment savedComment = commentRepository.save(comment);

        // 댓글 수 증가
        Post post = savedComment.getPost();
        post.incrementCommentCount();
        postRepository.save(post);

        return convertToDto(savedComment);
    }

    // 댓글 수정
    public CommentDto updateComment(Long commentNum, String content) {
        Comment comment = commentRepository.findById(commentNum)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        comment.setContent(content);
        comment.setModDate(LocalDate.now());
        return convertToDto(commentRepository.save(comment));
    }

    // 댓글 삭제
    public void deleteComment(Long commentNum) {
        Comment comment = commentRepository.findById(commentNum)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (comment.getReplies().isEmpty()) {
            // 대댓글이 없는 경우 실제 삭제
            commentRepository.delete(comment);
        } else {
            // 대댓글이 있는 경우 논리적 삭제
            comment.setDeleted(true);
            commentRepository.save(comment);
        }

        // 댓글 수 감소
        Post post = comment.getPost();
        post.decrementCommentCount();
        postRepository.save(post);
    }

    private CommentDto convertToDtoWithReplies(Comment comment) {
        List<CommentDto> replies = comment.getReplies().stream()
                .map(this::convertToDtoWithReplies).collect(Collectors.toList());

        // 댓글이 삭제되었고, 대댓글이 있는 경우
        String content = comment.isDeleted() && !replies.isEmpty() ? "삭제된 댓글입니다." : comment.getContent();

        // 댓글이 삭제되었고, 대댓글이 없는 경우 필터링을 위해 삭제된 상태 표시
        Boolean deleted = comment.isDeleted() && replies.isEmpty() ? true : comment.isDeleted();

        return new CommentDto(comment.getCommentNum(), comment.getPost().getPostNum(),
                                comment.getUser() != null ? comment.getUser().getUserNum() : null,
                                comment.getPartner() != null ? comment.getPartner().getPartnerNum() : null,
                                comment.getAdmin() != null ? comment.getAdmin().getAdminNum() : null,
                                comment.getUser() != null ? comment.getUser().getUserNickname() : null,
                                comment.getPartner() != null ? comment.getPartner().getCompanyName() : null,
                                comment.getAdmin() != null ? comment.getAdmin().getAdminName() : null,
                                content, comment.getRegDate(), comment.getModDate(),
                                comment.getParentComment() != null ? comment.getParentComment().getCommentNum() : null,
                                deleted, replies);
    }

    private Comment convertToEntity(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setCommentNum(commentDto.getCommentNum());
        comment.setContent(commentDto.getContent());
        comment.setRegDate(commentDto.getRegDate());
        comment.setModDate(commentDto.getModDate());
        comment.setDeleted(commentDto.getDeleted());

        Post post = postRepository.findById(commentDto.getPostNum()).orElseThrow(() -> new RuntimeException("Post not found"));
        comment.setPost(post);

        if (commentDto.getUserNum() != null) {
            User user = userRepository.findById(commentDto.getUserNum()).orElseThrow(() -> new RuntimeException("User not found"));
            comment.setUser(user);
        } else if (commentDto.getPartnerNum() != null) {
            Partner partner = partnerRepository.findById(commentDto.getPartnerNum()).orElseThrow(() -> new RuntimeException("Partner not found"));
            comment.setPartner(partner);
        } else if (commentDto.getAdminNum() != null) {
            Admin admin = adminRepository.findById(commentDto.getAdminNum()).orElseThrow(() -> new RuntimeException("Admin not found"));
            comment.setAdmin(admin);
        }

        if (commentDto.getParentCommentNum() != null) {
            Comment parentComment = commentRepository.findById(commentDto.getParentCommentNum()).orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        return comment;
    }

    // 특정 댓글 조회
    public Optional<CommentDto> getCommentById(Long commentNum) {
        return commentRepository.findById(commentNum).map(this::convertToDto);
    }

    private CommentDto convertToDto(Comment comment) {
        return new CommentDto(comment.getCommentNum(), comment.getPost().getPostNum(),
                comment.getUser() != null ? comment.getUser().getUserNum() : null,
                comment.getPartner() != null ? comment.getPartner().getPartnerNum() : null,
                comment.getAdmin() != null ? comment.getAdmin().getAdminNum() : null,
                comment.getUser() != null ? comment.getUser().getUserNickname() : null,
                comment.getPartner() != null ? comment.getPartner().getCompanyName() : null,
                comment.getAdmin() != null ? comment.getAdmin().getAdminName() : null,
                comment.getContent(), comment.getRegDate(), comment.getModDate(),
                comment.getParentComment() != null ? comment.getParentComment().getCommentNum() : null,
                comment.isDeleted(), null);
    }
}
