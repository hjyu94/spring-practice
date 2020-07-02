package me.hjeong.fileutilpractice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hjeong.fileutilpractice.config.AppProperties;
import me.hjeong.fileutilpractice.util.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UploadController {

    private final AppProperties appProperties;

    @GetMapping("/uploadForm")
    public String uploadFormGet() {
        log.info("upload GET ..........");
        return "uploadForm";
    }

    @PostMapping(value = "/uploadForm")
    public String uploadFormPost(MultipartFile file, Model model) throws IOException {
        log.info("upload POST .........originalName={}, size={}, contentType={}"
                , file.getOriginalFilename()
                , file.getSize()
                , file.getContentType());

        String savedFileName = FileUtils.uploadFile(file, appProperties.getUploadRootPath());
        model.addAttribute("savedFileName", savedFileName);
        return "uploadForm";
    }


    @ResponseBody
    @PostMapping(value = "/uploadAjax", produces = "text/plain;charset=UTF-8")
    // 한글 파일명을 지원하기 위해 파일명을 UTF-8 인코딩해서 리턴한다.
    public ResponseEntity<String> uploadFormAJAX(MultipartFile file, String type) {
        log.info("upload AJAX ...... originalName={}, size={}, contentType={}"
                , file.getOriginalFilename() // "cat.png"
                , file.getSize() // 2894464 -> 2894KB -> 대충 2.8MB
                , file.getContentType()); // "image/png"
        log.info("......type={}", type);
        try {
            String savedFileName = FileUtils.uploadFile(file, appProperties.getUploadRootPath());
            return ResponseEntity.ok().body(savedFileName);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}