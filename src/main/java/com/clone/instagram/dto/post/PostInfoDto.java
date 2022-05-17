package com.clone.instagram.dto.post;

import com.clone.instagram.entity.Comment;
import com.clone.instagram.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PostInfoDto {

    private Long id;
    private String text;
    private String tag;
    private LocalDateTime createdate;
    private User postUploader;
    private Long likesCount;
    private boolean likeState;
    private boolean uploader;
    private String postImgUrl;
    private List<Comment> commentList;
}
