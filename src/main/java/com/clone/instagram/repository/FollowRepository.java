package com.clone.instagram.repository;

import com.clone.instagram.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findFollowByFromUserIdAndToUserId(Long from_user_id, Long to_user_id);

    @Query(value = "SELECT COUNT(*) FROM follow WHERE to_user_id = ?", nativeQuery = true)
    int findFollowerCountById(Long profileId);

    @Query(value = "SELECT COUNT(*) FROM follow WHERE from_user_id = ?", nativeQuery = true)
    int findFollowingCountById(Long profileId);

    @Modifying
    @Query(value = "INSERT INTO follow(from_user_id, to_user_id) VALUES(?, ?)", nativeQuery = true)
    void follow(Long fromId, Long toId);

    @Modifying
    @Query(value = "DELETE FROM follow WHERE from_user_id = ? AND to_user_id = ?", nativeQuery = true)
    void unFollow(Long fromId, Long toId);

    @Query(value = "SELECT to_user_id FROM follow where from_user_id = ?", nativeQuery = true)
    List<Long> findToUserByFromUser(Long sessionId);
}
