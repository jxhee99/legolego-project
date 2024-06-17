package com.kosta.legolego.user.dto;

import com.kosta.legolego.user.entity.SiteUser;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {
    private int userNum;
    private String userEmail;
    private String userPw;
    private String username;
    private String userNickname;
    private String userPhone;
    private SiteUser.UserStatus userStatus;


}
