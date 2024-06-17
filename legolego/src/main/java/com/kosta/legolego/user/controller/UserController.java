package com.kosta.legolego.user.controller;


import com.kosta.legolego.user.UserCreateForm;
import com.kosta.legolego.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")

public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "user/signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/signup_form";
        }

        if (!userCreateForm.getUserPw1().equals(userCreateForm.getUserPw2())){
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "user/signup_form";
        }


        userService.create(userCreateForm.getUserEmail(),
                userCreateForm.getUserPw1(), userCreateForm.getUserName(), userCreateForm.getUserNickname(), userCreateForm.getUserPhone());


        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return"user/login_form";
    }

    @GetMapping("/success")
    public String success(){
        return "user/success";
    }
}
