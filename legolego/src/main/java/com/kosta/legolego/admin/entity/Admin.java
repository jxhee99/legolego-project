package com.kosta.legolego.admin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Admin {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "admin_num")
        private Long adminNum;

        @Column(name = "admin_email", nullable = false, unique = true, length = 100)
        private String adminEmail;

        @Column(name = "admin_pw", nullable = false, length = 255)
        private String adminPw;

        @Column(name = "admin_name", nullable = false, length = 50)
        private String adminName;

        @Column(name = "admin_aproval", nullable = false)
        private Boolean adminApproval = false;
    
}
