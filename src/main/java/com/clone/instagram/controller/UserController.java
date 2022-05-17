package com.clone.instagram.controller;

import com.clone.instagram.config.auth.PrincipalDetails;
import com.clone.instagram.dto.user.UserProfileDto;
import com.clone.instagram.dto.user.UserUpdateDto;
import com.clone.instagram.entity.User;
import com.clone.instagram.service.UserService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 로그인 후 나타나는 메인 화면
     * @return
     */
    @GetMapping(value = {"/", "/story"}) // 다중 맵핑
    public String story() {
        return "post/story";
    }

    /**
     * 사용자 프로필 화면으로 이동
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/user/profile/{id}")
    public String profile(Model model, @PathVariable("id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        UserProfileDto userProfileDto = userService.getUserProfile(id, principalDetails.getUser().getId());
        model.addAttribute("userProfileDto", userProfileDto);
        return "user/profile";
    }

    /**
     * 사용자 수정화면으로 이동
     * @return
     */
    @GetMapping("/user/update")
    public String update() {
        return "/user/update";
    }

    @PostMapping("/user/update")
    public String updateProfile(UserUpdateDto userUpdateDto, @RequestParam("profileImgUrl") MultipartFile profileImgUrl, PrincipalDetails principalDetails) throws IOException {
        userService.update(userUpdateDto, profileImgUrl, principalDetails);
        return "redirect:/user/profile/" + userUpdateDto.getId();
    }

    @Value("${profileImg.path}")
    private String profileUploadFolder;

    @GetMapping("/profile_img/{profileUrl}")
    public ResponseEntity<byte[]> profileImg(@PathVariable("profileUrl") String profileUrl, HttpServletResponse response) throws IOException {

        String file = profileUploadFolder + profileUrl;

        response.setHeader("Content-Disposition", "attachment; filename=\""+ profileUrl +"\"");
        response.setContentType("application/octet-stream");

        ServletOutputStream sos = response.getOutputStream();

        File f = new File(file);
        FileInputStream fis = new FileInputStream(f);
        byte[] imageByteArray = IOUtils.toByteArray(fis);
//        byte[] b = new byte[512];
//        int len;
//
//        while ((len = fis.read(b)) != -1) {
//            sos.write(b, 0, len);
//        }
//
        sos.close();
        fis.close();
        return new ResponseEntity<byte[]>(imageByteArray, (HttpHeaders)response, HttpStatus.OK);


    }
}
