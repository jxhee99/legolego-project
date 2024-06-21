package com.kosta.legolego.user.controller;


import com.kosta.legolego.user.UserCreateForm;
import com.kosta.legolego.user.service.UserService;
import com.kosta.legolego.user.service.PasswordResetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller

@RequestMapping("/user")

public class UserController {
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @GetMapping("/signup")
    public ResponseEntity<String> signup(UserCreateForm userCreateForm) {

        return ResponseEntity.status(HttpStatus.OK).body("user/signup_form");
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.OK).body("user/signup_form");
        }

        if (!userCreateForm.getUserPw1().equals(userCreateForm.getUserPw2())){
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.OK).body("user/signup_form");
        }


        userService.create(userCreateForm.getUserEmail(),
                userCreateForm.getUserPw1(), userCreateForm.getUserName(), userCreateForm.getUserNickname(), userCreateForm.getUserPhone());


        return ResponseEntity.status(HttpStatus.OK).body("redirect:/");
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(){
        return ResponseEntity.status(HttpStatus.OK).body("user/login_form");
    }

    @GetMapping("/success")
    public ResponseEntity<String> success(){

        return ResponseEntity.status(HttpStatus.OK).body("user/success");
    }

    @GetMapping("/find-email")
    public ResponseEntity<String> findEmailForm() {

        return ResponseEntity.status(HttpStatus.OK).body("user/find_email_form");
    }

    @PostMapping("/find-email")
    public ResponseEntity<String> findEmail(@RequestParam("userName") String userName, @RequestParam("userPhone") String userPhone, Model model) {
        String userEmail = userService.findUserEmail(userName, userPhone);
        model.addAttribute("userEmail", userEmail);
        return ResponseEntity.status(HttpStatus.OK).body("user/show_email");
    }

    @GetMapping("/find-password")
    public ResponseEntity<String> findPasswordForm() {
        return ResponseEntity.status(HttpStatus.OK).body("user/find_password_form");
    }



}
