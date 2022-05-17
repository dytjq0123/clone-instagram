package com.clone.instagram.service;

import com.clone.instagram.config.auth.PrincipalDetails;
import com.clone.instagram.entity.Comment;
import com.clone.instagram.entity.Post;
import com.clone.instagram.entity.User;
import com.clone.instagram.exception.CustomApiException;
import com.clone.instagram.repository.CommentRepository;
import com.clone.instagram.repository.PostRepository;
import com.clone.instagram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PostRepository postRepository;

    @Transactional
    public Comment addComment (String text, Long postId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Post post = postRepository.findById(postId).get();
        User user = principalDetails.getUser();
        Comment comment = Comment.builder()
                .text(text)
                .post(post)
                .user(user)
                .build();
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(long id) {
        commentRepository.deleteById(id);
    }
}
