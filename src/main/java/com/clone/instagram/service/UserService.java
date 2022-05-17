package com.clone.instagram.service;

import com.clone.instagram.config.auth.PrincipalDetails;
import com.clone.instagram.dto.user.UserProfileDto;
import com.clone.instagram.dto.user.UserSignupDto;
import com.clone.instagram.dto.user.UserUpdateDto;
import com.clone.instagram.entity.User;
import com.clone.instagram.exception.CustomValidationException;
import com.clone.instagram.repository.FollowRepository;
import com.clone.instagram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private FollowRepository followRepository;


    @Autowired
    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.followRepository = followRepository;
    }

    /**
     * 회원 가입 기능
     * @param userSignupDto
     */
    @Transactional
    public void save(UserSignupDto userSignupDto) {

        // 아이디(이메일) 중복 체크
        validateDuplicate(userSignupDto);

        userRepository.save(User.builder()
                .email(userSignupDto.getEmail())
                .password(passwordEncoder.encode(userSignupDto.getPassword()))
                .phone(userSignupDto.getPhone())
                .name(userSignupDto.getName())
                .title(null)
                .website(null)
                .profileImgUrl("/img/default_profile.png")
                .build());
    }

    /**
     * 회원 가입시 아이디(이메일) 중복 체크
     * @param userSignupDto
     */
    private void validateDuplicate(UserSignupDto userSignupDto) {
        User user = userRepository.findByEmail(userSignupDto.getEmail());
        if(user != null){
            throw new IllegalStateException("이미 존재하는 사용자입니다.");
        }
    }

    @Value("${profileImg.path}")
    private String uploadFolder;

    /**
     * 사용자 업데이트 기능
     * @param userUpdateDto
     * @param file
     */
    @Transactional
    public void update(UserUpdateDto userUpdateDto, MultipartFile file, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        try {
            // 사용자 불러오기 없으면 Exception
            User user = userRepository.findById(userUpdateDto.getId()).orElseThrow(() -> new Exception("존재하지 않는 사용자 입니다."));

            if(!file.isEmpty()){ // 파일이 업로드 되었는지 확인
                // 프로필 이미지가 저장될 경로
//                String profileImgPath = System.getProperty("user.dir") + "/src/main/resources/static/profileImgFiles";
                String profileImgPath = uploadFolder;

                // 파일이 저장될 디렉토리가 없는 경우 디렉토리 생성
                File newFile = new File(profileImgPath);
                if(!newFile.exists()){
                    newFile.mkdirs();
                }

                // 파일명 암호화
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + file.getOriginalFilename();

                if(!user.getProfileImgUrl().equalsIgnoreCase("/img/default_profile.png")){ // 저장된 프로필 사진이 존재하는 경우
                    File savedFile = new File(profileImgPath + user.getProfileImgUrl());
                    savedFile.delete(); // 이미 존재하는 프로필 사진 삭제
                }

                // 파일 객체 생성
                File saveFile = new File(profileImgPath, fileName);

                // 파일 저장
                file.transferTo(saveFile);

                // 프로필 이미지 파일 불러올떄 사용될 경로
                user.updateProfileUrl(fileName); // 사용자 이미지 경로 업데이트
            }

            // 나머지 사용자 정보 업데이트
            user.update(
                    passwordEncoder.encode(userUpdateDto.getPassword()),
                    userUpdateDto.getPhone(),
                    userUpdateDto.getName(),
                    userUpdateDto.getTitle(),
                    userUpdateDto.getWebsite()
            );


            // 세션 재등록
            principalDetails.updateUser(user);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 사용자 상세조회
     * @param profileId
     * @param sessionId
     * @return
     */
    public UserProfileDto getUserProfile(Long profileId, Long sessionId) {

        User user = userRepository.findById(profileId).orElseThrow(() -> { return new CustomValidationException("찾을 수 없는 user입니다.");});
        int postCount = user.getPostList().size();

        // loginEmail 활용하여 currentId가 로그인된 사용자 인지 확인
        User loginUser = userRepository.findById(sessionId).orElseThrow(() -> { return new CustomValidationException("찾을 수 없는 user입니다.");});
        boolean login = loginUser.getId() == user.getId() ? true : false;


        // currentId를 가진 user가 loginEmail을 가진 user를 구독 했는지 확인
        boolean follow = followRepository.findFollowByFromUserIdAndToUserId(loginUser.getId(), user.getId()) != null ? true : false;

        //currentId를 가진 user의 팔로워, 팔로잉 수를 확인한다.
        int userFollowerCount = followRepository.findFollowerCountById(profileId);
        int userFollowingCount = followRepository.findFollowingCountById(profileId);


        UserProfileDto userProfileDto = new UserProfileDto(login, follow, user, postCount, userFollowerCount, userFollowingCount);


        //좋아요 수 확인
        user.getPostList().forEach(post -> {
            post.updateLikesCount((long) post.getLikesList().size());
        });

        return userProfileDto;

    }





}
