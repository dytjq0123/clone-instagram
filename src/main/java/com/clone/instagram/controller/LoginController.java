package com.clone.instagram.controller;

import com.clone.instagram.entity.User;
import com.clone.instagram.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @GetMapping("/join")
    public String joinView() {
        return "join";
    }

    @PostMapping("/join")
    public String join(User user) {
        if(userService.save(user)){
            return "redirect:/login";
        }else {
            return "redirect:/join?error";
        }
    }

    @PostMapping("/login")
    public String login() {
        return "main";
    }
}
