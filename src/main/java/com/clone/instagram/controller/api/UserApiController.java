package com.clone.instagram.controller.api;

import com.clone.instagram.config.auth.PrincipalDetails;
import com.clone.instagram.entity.User;
import com.clone.instagram.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserApiController {

    private final FollowService followService;

    @GetMapping("/user/{profileId}/follower")
    public ResponseEntity<?> getFollower(@PathVariable Long profileId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return new ResponseEntity<>(followService.getFollower(profileId, principalDetails.getUser().getId()), HttpStatus.OK);
    }

    @GetMapping("/user/{profileId}/following")
    public ResponseEntity<?> getFollowing(@PathVariable Long profileId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return new ResponseEntity<>(followService.getFollowing(profileId, principalDetails.getUser().getId()), HttpStatus.OK);
    }
}
