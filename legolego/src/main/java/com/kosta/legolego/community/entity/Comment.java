package com.kosta.legolego.community.entity;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_num")
    private Long commentNum;

    @ManyToOne
    @JoinColumn(name = "post_num")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_num")
    private User user;

    @ManyToOne
    @JoinColumn(name = "partner_num")
    private Partner partner;

    @ManyToOne
    @JoinColumn(name = "admin_num")
    private Admin admin;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "reg_date", nullable = false)
    private LocalDate regDate;

    @Column(name = "mod_date")
    private LocalDate modDate;

    @ManyToOne
    @JoinColumn(name = "parent_comment_num")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> replies;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    public Boolean isDeleted() {
        return deleted;
    }
}
