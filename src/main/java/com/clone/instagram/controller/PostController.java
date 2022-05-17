package com.clone.instagram.controller;

import com.clone.instagram.config.auth.PrincipalDetails;
import com.clone.instagram.dto.post.PostDto;
import com.clone.instagram.dto.post.PostUpdateDto;
import com.clone.instagram.dto.post.PostUploadDto;
import com.clone.instagram.exception.CustomValidationException;
import com.clone.instagram.service.PostService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequiredArgsConstructor
public class PostController {

    @Autowired
    private final PostService postService;

    //포스트 업로드 화면으로 이동
    @GetMapping("/post/upload")
    public String upload() {
        return "post/upload";
    }

    //포스트 업로드 후 프로필 화면으로 이동
    @PostMapping("post")
    public String uploadPost(PostUploadDto postUploadDto, @RequestParam("uploadImgUrl") MultipartFile multipartFile,
                             RedirectAttributes redirectAttributes, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if(multipartFile.isEmpty()) {
            throw new CustomValidationException("이미지가 첨부되지 않았습니다");
        }
        postService.save(postUploadDto, multipartFile, principalDetails);
        return "redirect:/user/profile/" + principalDetails.getUser().getId();
    }

    //포스트 수정 화면으로 이동
    @GetMapping("/post/update/{postId}")
    public String update(@PathVariable Long postId, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        PostDto postDto = postService.getPostDto(postId, principalDetails);
        model.addAttribute("postDto", postDto);
        return "post/update";
    }

    //포스트 수정 폼
    @PostMapping("/post/update")
    public String postUpdate(PostUpdateDto postUpdateDto, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        postService.update(postUpdateDto);
        return "redirect:/user/profile/" + principalDetails.getUser().getId();
    }

    //포스트 삭제 폼
    @PostMapping("/post/delete")
    public String delete(@RequestParam("postId") Long postId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        postService.delete(postId);
        return "redirect:/user/profile/" + principalDetails.getUser().getId();
    }

    //검색 페이지 - 게시글의 태그 눌러서 이동
    @GetMapping("/post/search")
    public String search(@RequestParam("tag") String tag, Model model) {
        model.addAttribute("tag", tag);
        return "post/search";
    }

    //검색 폼 입력 후 페이지 이동
    @PostMapping("/post/searchForm")
    public String searchForm(String tag, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("tag", tag);
        return "redirect:/post/search";
    }

    //좋아요한 포스트 출력 페이지로 이동
    @GetMapping("/post/likes")
    public String likes() {
        return "post/likes";
    }

    //인기 포스트 페이지로 이동
    @GetMapping("/post/popular")
    public String popular() {
        return "post/popular";
    }

    @Value("${post.path}")
    private String postUploadFolder;

    @GetMapping("/upload/{postUrl}")
    public void postImg(@PathVariable("postUrl") String postUrl, HttpServletResponse response) throws IOException {

        String filePath = postUploadFolder + postUrl;

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
