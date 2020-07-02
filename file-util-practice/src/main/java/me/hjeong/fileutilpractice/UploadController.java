package me.hjeong.fileutilpractice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UploadController {

    @GetMapping("/uploadForm")
    public String uploadFormGet() {
        log.info("upload GET ..........");
        return "uploadForm";
    }

}