package com.clone.instagram.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@ToString(of = {"id", "tag", "text"})
public class Post extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String postImgUrl;
    private String tag;
    private String text;

//    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) // 여러개의 포스트와 하나의 유저 연결 관계
    @JoinColumn(name = "user_id") // 연결할 외래키 = 필드명_(유니크값)컬럼명
//    @JsonIgnoreProperties({"postList"})
    private User user;

//    @JsonIgnore
    @OneToMany(mappedBy = "post")
    @JsonIgnoreProperties({"post"})
    private List<Likes> likesList = new ArrayList<>();

//    @JsonIgnore
    @OrderBy("id")
    @OneToMany(mappedBy = "post")
    @JsonIgnoreProperties({"post"})
    private List<Comment> commentList = new ArrayList<>();

    @Transient // table column 으로 사용되지 않는 필드
    private Long likesCount;

    @Transient // table column 으로 사용되지 않는 필드
    private boolean likesState;

    @Builder
    public Post(String postImgUrl, String tag, String text, User user, Long likesCount) {
        this.postImgUrl = postImgUrl;
        this.tag = tag;
        this.text = text;
        this.user = user;
        this.likesCount = likesCount;
    }

    public void update(String tag, String text) {
        this.tag = tag;
        this.text = text;
    }

    public void updateLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }

    public void updateLikesState(boolean likesState) {
        this.likesState = likesState;
    }

    public void commentList(List<Comment> commentList) {
        this.getCommentList().addAll(commentList);
    }

}
