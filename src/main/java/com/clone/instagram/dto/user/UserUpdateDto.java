package com.clone.instagram.dto.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    // 회원 정보 수정 시 사용되는 Dto

    private Long id;
    private String password;
    private String phone;
    private String name;
    private String title;
    private String website;
}
