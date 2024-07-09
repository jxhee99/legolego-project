package com.kosta.legolego.community.entity;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Post {

    public enum PostCategory {
        RECRUITMENT,  // 동행모집
        INQUIRY,  // 여행문의
        TIP,  // 여행 팁
        ROUTE,  // 여행 경로 공유
        NOTICE,  // (관리자) 공지사항
        EVENT  // (여행사) 이벤트 및 할인 정보
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_num")
    private Long postNum;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_num")
    private User user;

    @ManyToOne
    @JoinColumn(name = "partner_num")
    private Partner partner;

    @ManyToOne
    @JoinColumn(name = "admin_num")
    private Admin admin;

    @Column(name = "reg_date", nullable = false)
    private LocalDate regDate;

    @Column(name = "mod_date")
    private LocalDate modDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private PostCategory postCategory;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments;
}
