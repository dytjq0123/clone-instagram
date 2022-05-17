package com.clone.instagram.repository;

import com.clone.instagram.entity.Comment;
import com.clone.instagram.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteCommentsByPost(Post post);

    @EntityGraph(attributePaths = {"user"})
    List<Comment> findByPostId(Long postId);
}
