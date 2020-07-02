package me.hjeong.fileutilpractice.util;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUtils {

    public static String uploadFile(MultipartFile file, String uploadPath) throws IOException {
        // uploadPath: F:/Lecture/spring-practice/file-util-practice/upload
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File target = new File(uploadPath, filename);
        FileCopyUtils.copy(file.getBytes(), target);
        return filename;
    }

}