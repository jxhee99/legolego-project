package com.kosta.legolego.user.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter

public class SiteUser{

        public enum UserStatus{
            registered,
            withdrawal
        }
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_num")
        private long userNum;

        @Column(name = "user_email", nullable = false, unique = true)
        private String userEmail;

        @Column(name = "user_pw", nullable = false)
        private String userPw;

        @Column(name = "user_name", nullable = false)
        private String username;

        @Column(name = "user_nickname", nullable = false)
        private String userNickname;



        @Column(name = "user_phone", nullable = false)
        private String userPhone;

        @Enumerated(EnumType.STRING)
        @Column(name = "user_status")
        private com.kosta.legolego.user.entity.SiteUser.UserStatus userStatus = UserStatus.registered;



        @Builder
        public SiteUser(String userEmail, String userPw, String username, String userNickname, String userPhone) {
                this.userEmail = userEmail;
                this.userPw = userPw;
                this.username = username;
                this.userNickname = userNickname;
                this.userPhone = userPhone;

        }

}




