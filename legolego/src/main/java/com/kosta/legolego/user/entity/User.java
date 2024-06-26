package com.kosta.legolego.user.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter

public class User {

    public enum UserStatus{
        registered,
        withdrawal
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_num")
    private long userNum;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private User.UserStatus userStatus = UserStatus.registered;



    @Builder
    public User(String userEmail, String userPw, String userName, String userNickname, String userPhone) {
        this.userEmail = userEmail;
        this.userPw = userPw;
        this.userName = userName;
        this.userNickname = userNickname;
        this.userPhone = userPhone;

    }

}



