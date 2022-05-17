package com.clone.instagram.service;

import com.clone.instagram.exception.CustomApiException;
import com.clone.instagram.repository.LikesRepository;
import com.clone.instagram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;

    @Transactional
    public void likes(long postId, long sessionId) {
        try {
            likesRepository.likes(postId, sessionId);
        } catch (Exception e) {
            throw new CustomApiException("이미 좋아요 하였습니다.");
        }
    }

    @Transactional
    public void unLikes(long postId, long sessionId) {
        likesRepository.unLikes(postId, sessionId);
    }


}
