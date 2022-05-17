package com.clone.instagram.repository;

import com.clone.instagram.entity.Follow;
import com.clone.instagram.entity.Post;
import com.clone.instagram.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

//    @Query(value = "SELECT * FROM post WHERE user_id IN (SELECT to_user_id FROM FOLLOW WHERE from_user_id = ?) ORDER BY post_id DESC", nativeQuery = true)
//    Page<Post> mainStory(Long sessionId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Post> findByUserIdIn(List userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Post> findByTagContaining(String tag, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Optional<Post> findPostById(@Param("postId") Long postId);

}
