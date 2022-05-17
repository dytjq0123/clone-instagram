package com.clone.instagram.service;

import com.clone.instagram.dto.follow.FollowDto;
import com.clone.instagram.exception.CustomApiException;
import com.clone.instagram.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.qlrm.mapper.JpaResultMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final EntityManager em;

    @Transactional
    public void follow(Long fromUserId, Long toUserId) {
        if(followRepository.findFollowByFromUserIdAndToUserId(fromUserId, toUserId) != null) throw new CustomApiException("이미 팔로우 하였습니다.");
        followRepository.follow(fromUserId, toUserId);
    }

    @Transactional
    public void unFollow(Long fromUserId, Long toUserId) {
        followRepository.unFollow(fromUserId, toUserId);
    }

    @Transactional
    public List<FollowDto> getFollower(Long profileId, Long loginId) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT u.user_id, u.name, u.profile_img_url, ");
        sb.append("if ((SELECT 1 FROM follow WHERE from_user_id = ? AND to_user_id = u.user_id), TRUE, FALSE) AS followState, ");
        sb.append("if ((?=u.user_id), TRUE, FALSE) AS loginUser ");
        sb.append("FROM user u, follow f ");
        sb.append("WHERE u.user_id = f.from_user_id AND f.to_user_id = ?");
        // 쿼리 완성
        Query query = em.createNativeQuery(sb.toString())
                .setParameter(1, loginId)
                .setParameter(2, loginId)
                .setParameter(3, profileId);

        //JPA 쿼리 매핑 - DTO에 매핑
        JpaResultMapper result = new JpaResultMapper();
        List<FollowDto> followDtoList = result.list(query, FollowDto.class);
        return followDtoList;
    }

    @Transactional
    public List<FollowDto> getFollowing(Long profileId, Long loginId) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT u.user_id, u.name, u.profile_img_url, ");
        sb.append("if ((SELECT 1 FROM follow WHERE from_user_id = ? AND to_user_id = u.user_id), TRUE, FALSE) AS followState, ");
        sb.append("if ((?=u.user_id), TRUE, FALSE) AS loginUser ");
        sb.append("FROM user u, follow f ");
        sb.append("WHERE u.user_id = f.to_user_id AND f.from_user_id = ?");

        // 쿼리 완성
        Query query = em.createNativeQuery(sb.toString())
                .setParameter(1, loginId)
                .setParameter(2, loginId)
                .setParameter(3, profileId);

        //JPA 쿼리 매핑 - DTO에 매핑
        JpaResultMapper result = new JpaResultMapper();
        List<FollowDto> followDtoList = result.list(query, FollowDto.class);
        return followDtoList;
    }


}
