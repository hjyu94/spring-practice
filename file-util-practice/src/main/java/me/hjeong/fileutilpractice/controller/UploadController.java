package me.hjeong.fileutilpractice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
        return "youtube/uploadForm";
    }
}