package com.clone.instagram.repository;

import com.clone.instagram.entity.Likes;
import com.clone.instagram.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    void deleteLikesByPost(Post post);

    @Modifying
    @Query(value = "INSERT INTO likes(post_id, user_id) VALUES(?, ?)", nativeQuery = true)
    void likes(Long postId, Long userId);

    @Modifying
    @Query(value = "DELETE FROM likes WHERE post_id = ? AND user_id = ?", nativeQuery = true)
    void unLikes(Long postId, Long userId);
}
