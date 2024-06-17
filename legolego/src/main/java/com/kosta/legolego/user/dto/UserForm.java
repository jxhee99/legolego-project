package com.kosta.legolego.user.dto;

import com.kosta.legolego.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {
    private int userNum;
    private String userEmail;
    private String userPw;
    private String userName;
    private String userNickname;
    private String userPhone;
    private User.UserStatus userStatus;


}
