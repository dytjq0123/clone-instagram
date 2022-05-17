package com.clone.instagram.dto.post;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PostUploadDto {

    private String text;
    private String tag;
}
