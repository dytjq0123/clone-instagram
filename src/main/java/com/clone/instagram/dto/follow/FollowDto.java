package com.clone.instagram.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Builder
@AllArgsConstructor
public class FollowDto {

    private Long id;
    private String name;
    private String profileImgUrl;
    private int followState;
    private int loginUser;

    public FollowDto(BigInteger id, String name, String profileImgUrl, int followState, int loginUser) {
        this.id = id.longValue();
        this.name = name;
        this.profileImgUrl = profileImgUrl;
        this.followState = followState;
        this.loginUser = loginUser;
    }
}
