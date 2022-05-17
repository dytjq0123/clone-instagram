package com.clone.instagram.dto.user;

import com.clone.instagram.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {

    private boolean loginUser; // 현재 프로필 페이지가 로그인한 유저의 프로필 페이지인지 확인
    private boolean follow; // 현재 프로필 페이지의 사용자를 팔로우 한 상태인지 확인
    @JsonIgnore
    private User user;
    private int postCount;
    private int userFollowerCount;
    private int userFollowingCount;

}
