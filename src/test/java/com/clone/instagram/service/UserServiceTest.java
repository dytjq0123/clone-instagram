package com.clone.instagram.service;

import com.clone.instagram.config.auth.PrincipalDetails;
import com.clone.instagram.config.auth.PrincipalDetailsService;
import com.clone.instagram.dto.user.UserSignupDto;
import com.clone.instagram.entity.User;
import com.clone.instagram.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    PrincipalDetailsService principalDetailsService;

    @Autowired
    UserRepository userRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void save() {
        // given
        String sEmail = "test@gmail.com";
        String sPass = "test12!@";
        UserSignupDto userSignupDto = new UserSignupDto(sEmail, sPass, "", "");

        // when
        userService.save(userSignupDto);

        // then
        User user = (User) principalDetailsService.loadUserByUsername(sEmail);
        assertThat(userSignupDto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void duplicateCheck() {
        // given
        UserSignupDto userSignupDto1 = new UserSignupDto("test1@naver.com", "12345", "", "");

        UserSignupDto userSignupDto2 = new UserSignupDto("test1@naver.com", "12345", "", "");

        // when
        userService.save(userSignupDto1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> userService.save(userSignupDto2));

        // then
//        assertThat(e.getMessage()).isEqualTo("이미 존재하는 사용자입니다.111");
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 사용자입니다.");
    }
}