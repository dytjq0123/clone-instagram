package com.clone.instagram.controller;

import com.clone.instagram.dto.user.UserSignupDto;
import com.clone.instagram.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    /**
     * 로그인 화면
     *
     * @return
     */
    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    /**
     * 회원 가입 화면
     *
     * @return
     */
    @GetMapping("/signup")
    public String signupView() {
        return "signup";
    }

    /**
     * 회원 가입 기능
     *
     * @param userSignupDto
     * @return
     */
    @PostMapping("/signup")
    public String signup(@Valid UserSignupDto userSignupDto, BindingResult bindingResult, Model model) {
        // 회원가입 정보 검증
        String signup = signupValidation(bindingResult, model);
        if (signup != null) {
            return signup;
        } else {
            try {
                userService.save(userSignupDto);
            } catch (Exception e) {
                return "redirect:/signup?error";
            }
            return "redirect:/login";
        }
    }

    /**
     * 회원 가입 데이터 검증
     *
     * @param bindingResult
     * @param model
     * @return
     */
    private String signupValidation(BindingResult bindingResult, Model model) {
        // 회원가입 검증 내용에 맞지 않아 에러가 발생한 경우
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError error : fieldErrors) {
                String field = error.getField();
                String message = error.getDefaultMessage();
                String rejectedValue = (String) error.getRejectedValue();
                // 잘못 입력된 데이터 signup.html에 다시 입력
                model.addAttribute("reject_" + field, rejectedValue);
                // signup.html 파일에 valid_ + field의 name 값을 키값으로
                // UserSignupDto 에서 정의 한 메세지 출력
                model.addAttribute("valid_" + field, message);
            }
            return "/signup";
        }
        return null;
    }

    /**
     * 로그아웃 기능
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
}
