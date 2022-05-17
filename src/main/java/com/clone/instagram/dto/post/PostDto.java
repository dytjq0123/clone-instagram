package com.clone.instagram.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PostDto {

    private Long id;
    private String tag;
    private String text;
    private String postImgUrl;

}
