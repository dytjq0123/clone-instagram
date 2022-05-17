package com.clone.instagram.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PostPreviewDto {

    private Long id;
    private String postImgUrl;
    private Long likesCount;

    public PostPreviewDto(BigInteger id, String postImgUrl, BigInteger likesCount) {
        this.id = id.longValue();
        this.postImgUrl = postImgUrl;
        this.likesCount = likesCount.longValue();
    }
}
