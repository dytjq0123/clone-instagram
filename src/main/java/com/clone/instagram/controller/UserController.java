package com.clone.instagram.controller;

import com.clone.instagram.config.auth.PrincipalDetails;
import com.clone.instagram.dto.user.UserProfileDto;
import com.clone.instagram.dto.user.UserUpdateDto;
import com.clone.instagram.entity.User;
import com.clone.instagram.service.UserService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

//    @Autowired
//    private AuthenticationManager authenticationManager;

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
    public String updateProfile(UserUpdateDto userUpdateDto, @RequestParam("profileImgUrl") MultipartFile profileImgUrl, @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        userService.update(userUpdateDto, profileImgUrl, principalDetails);

        User user = principalDetails.getUser();

        // 세션 등록
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principalDetails, principalDetails.getUser().getPassword());
//        Authentication authentication = authenticationManager.authenticate(token);
//        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/user/profile/" + userUpdateDto.getId();
    }

    @Value("${profileImg.path}")
    private String profileUploadFolder;

    @GetMapping("/profile_img/{profileUrl}")
    public void profileImg(@PathVariable("profileUrl") String profileUrl, HttpServletResponse response) throws IOException {

        String filePath = profileUploadFolder + profileUrl;

        File file = new File(filePath);
        if(!file.isFile()){
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write("<script type='text/javascript'>alert('조회된 정보가 없습니다.'); self.close();</script>");
            out.flush();
            return;
        }

        FileInputStream fis = null;
        new FileInputStream(file);

        BufferedInputStream in = null;
        ByteArrayOutputStream bStream = null;
        try {
            fis = new FileInputStream(file);
            in = new BufferedInputStream(fis);
            bStream = new ByteArrayOutputStream();
            int imgByte;
            while ((imgByte = in.read()) != -1) {
                bStream.write(imgByte);
            }

            String type = "";
            String ext = FilenameUtils.getExtension(file.getName());
            if (ext != null && !"".equals(ext)) {
                if ("jpg".equals(ext.toLowerCase())) {
                    type = "image/jpeg";
                } else {
                    type = "image/" + ext.toLowerCase();
                }

            }

            response.setHeader("Content-Type", type);
            response.setContentLength(bStream.size());

            bStream.writeTo(response.getOutputStream());

            response.getOutputStream().flush();
            response.getOutputStream().close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bStream != null) {
                try {
                    bStream.close();
                } catch (Exception est) {
                    est.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ei) {
                    ei.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception efis) {
                    efis.printStackTrace();
                }
            }
        }

    }
}
