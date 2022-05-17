package com.clone.instagram.service;

import com.clone.instagram.config.auth.PrincipalDetails;
import com.clone.instagram.dto.post.*;
import com.clone.instagram.entity.*;
import com.clone.instagram.exception.CustomValidationException;
import com.clone.instagram.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.qlrm.mapper.JpaResultMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final EntityManager em;

    @Value("${post.path}")
    private String uploadUrl;

    @Transactional
    public void save(PostUploadDto postUploadDto, MultipartFile multipartFile, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        // 파일이 저장될 디렉토리가 없는 경우 디렉토리 생성
        File newFile = new File(uploadUrl);
        if(!newFile.exists()){
            newFile.mkdirs();
        }

        UUID uuid = UUID.randomUUID();
        String imgFileName = uuid + "_" + multipartFile.getOriginalFilename();

        Path imageFilePath = Paths.get(uploadUrl + imgFileName);
        try {
            Files.write(imageFilePath, multipartFile.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        postRepository.save(Post.builder()
                .postImgUrl(imgFileName)
                .tag(postUploadDto.getTag())
                .text(postUploadDto.getText())
                .user(principalDetails.getUser())
                .likesCount(0L)
                .build());
    }

    @Transactional
    public PostInfoDto getPostInfoDto(Long postId, Long sessionId) {

        Post post = postRepository.findPostById(postId).get();

        //포스트 정보 요청시 포스트 엔티티의 likesCount, likesState, CommentList를 설정해준다.
        boolean likeState = false;
        for (Likes likes : post.getLikesList()) {
            if(likes.getUser().getId().equals(sessionId)) {
                likeState = true;
            }
        }

        //포스트 주인의 정보를 가져온다.
        User user = post.getUser();

        boolean upload = false;
        if(sessionId.equals(user.getId())) {
            upload = true;
        }

        PostInfoDto postInfoDto = new PostInfoDto(postId,
                post.getText(),
                post.getTag(),
                post.getCreateDate(),
                user,
                (long)post.getLikesList().size(),
                likeState,
                upload,
                post.getPostImgUrl(),
                post.getCommentList());

        return postInfoDto;
    }

    @Transactional
    public PostDto getPostDto(Long postId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Post post = postRepository.findById(postId).get();

        PostDto postDto = null;

        if(post.getUser() == principalDetails.getUser()){
            postDto = PostDto.builder()
                    .id(postId)
                    .tag(post.getTag())
                    .text(post.getText())
                    .postImgUrl(post.getPostImgUrl())
                    .build();
        }else {
            throw new CustomValidationException("Post 작성자가 아닙니다.");
        }


        return postDto;
    }

    @Transactional
    public void update(PostUpdateDto postUpdateDto) {
        Post post = postRepository.findById(postUpdateDto.getId()).get();
        post.update(postUpdateDto.getTag(), postUpdateDto.getText());
    }

    @Transactional
    public void delete(Long postId) {
        Post post = postRepository.findById(postId).get();

        //관련된 likes의 정보 먼저 삭제해 준다.
        likesRepository.deleteLikesByPost(post);

        //관련된 Comment의 정보 먼저 삭제해 준다.
        commentRepository.deleteCommentsByPost(post);

        //관련 파일 저장 위치에서 삭제해 준다.
        File file = new File(uploadUrl + post.getPostImgUrl());
        file.delete();

        postRepository.deleteById(postId);
    }

    @Transactional
    public Page<Post> getPost(Long sessionId, Pageable pageable) {
        // 현재 접속중인 유저가 팔로우중인 유저 리스트
        List<Long> toUserId = followRepository.findToUserByFromUser(sessionId);

        // 팔로우중인 유저들의 포스트
        Page<Post> postList = postRepository.findByUserIdIn(toUserId, pageable);


        postList.forEach(post -> {
            post.commentList(commentRepository.findByPostId(post.getId()));
            post.updateLikesCount((long) post.getLikesList().size());
            post.getLikesList().forEach(likes -> {
                if(likes.getUser().getId().equals(sessionId)) {
                    post.updateLikesState(true);
                }
            });
        });

        return postList;
    }

    @Transactional
    public Page<Post> getTagPost(@Param("tag") String tag, Long sessionId, Pageable pageable) {
        Page<Post> postList = postRepository.findByTagContaining(tag, pageable);

        postList.forEach(post -> {
            post.updateLikesCount((long) post.getLikesList().size());
            post.getLikesList().forEach(likes -> {
                if(likes.getUser().getId() == sessionId) {
                    post.updateLikesState(true);
                }
            });
        });
        return postList;
    }

    @Transactional
    public Page<PostPreviewDto> getLikesPost(Long sessionId, Pageable pageable) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT p.post_id, p.post_img_url, COUNT(p.post_id) as likesCount ");
        sb.append("FROM likes l, post p ");
        sb.append("WHERE l.post_id = p.post_id ");
        sb.append("AND p.post_id IN (SELECT p.post_id FROM likes l, post p WHERE l.user_id = ? AND p.post_id = l.post_id) ");
        sb.append("GROUP BY p.post_id ");
        sb.append("ORDER BY p.post_id");

        // 쿼리 완성
        Query query = em.createNativeQuery(sb.toString())
                .setParameter(1, sessionId);

        //JPA 쿼리 매핑 - DTO에 매핑
        JpaResultMapper result = new JpaResultMapper();
        List<PostPreviewDto> postLikesList = result.list(query, PostPreviewDto.class);

        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > postLikesList.size() ? postLikesList.size() : (start + pageable.getPageSize());

        if(start > postLikesList.size()) return new PageImpl<PostPreviewDto>(postLikesList.subList(0, 0), pageable, 0);

        Page<PostPreviewDto> postLikesPage = new PageImpl<>(postLikesList.subList(start, end), pageable, postLikesList.size());
        return postLikesPage;
    }

    @Transactional
    public List<PostPreviewDto> getPopularPost() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT p.post_id, p.post_img_url, COUNT(p.post_id) as likesCount ");
        sb.append("FROM likes l, post p ");
        sb.append("WHERE l.post_id = p.post_id ");
        sb.append("AND p.post_id IN (SELECT p.post_id FROM likes l, post p WHERE p.post_id = l.post_id) ");
        sb.append("GROUP BY p.post_id ");
        sb.append("ORDER BY likesCount DESC, p.post_id ");
        sb.append("LIMIT 12 ");

        // 쿼리 완성
        Query query = em.createNativeQuery(sb.toString());

        //JPA 쿼리 매핑 - DTO에 매핑
        JpaResultMapper result = new JpaResultMapper();
        List<PostPreviewDto> postPreviewDtoList = result.list(query, PostPreviewDto.class);

        return postPreviewDtoList;
    }
}
