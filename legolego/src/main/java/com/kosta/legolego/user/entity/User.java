package com.kosta.legolego.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_num")
    private Long userNum;

    @Column(name = "user_email", nullable = false, unique = true, length = 100)
    private String userEmail;

    @Column(name = "user_pw", nullable = false, length = 255)
    private String userPw;

    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    @Column(name = "user_nickname", nullable = false, length = 30)
    private String userNickname;

    @Column(name = "user_phone", nullable = false, length = 15)
    private String userPhone;

    @Column(name = "user_status", nullable = false)
    private Boolean userStatus = false;
}