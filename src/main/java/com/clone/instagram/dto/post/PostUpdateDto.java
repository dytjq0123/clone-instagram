package com.clone.instagram.dto.post;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PostUpdateDto {

    private Long id;
    private String tag;
    private String text;
}
