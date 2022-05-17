package com.clone.instagram.dto.comment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CommentUploadDto {

    @NotBlank
    private String text;

    @NotNull
    private Long postId;
}
