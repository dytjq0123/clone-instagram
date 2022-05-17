package com.clone.instagram.controller.api;

import com.clone.instagram.config.auth.PrincipalDetails;
import com.clone.instagram.entity.User;
import com.clone.instagram.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FollowApiController {

    private final FollowService followService;

    @PostMapping("/follow/{toUserId}")
    public ResponseEntity<?> followUser(@PathVariable Long toUserId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        followService.follow(principalDetails.getUser().getId(), toUserId);
        return new ResponseEntity<>("팔로우 성공", HttpStatus.OK);
    }

    @DeleteMapping("/follow/{toUserId}")
    public ResponseEntity<?> unFollowUser(@PathVariable Long toUserId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        followService.unFollow(principalDetails.getUser().getId(), toUserId);
        return new ResponseEntity<>("팔로우 취소 성공", HttpStatus.OK);
    }
}
