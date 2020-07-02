package me.hjeong.fileutilpractice.util;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

public class FileUtils {

    public static String uploadFile(MultipartFile file, String uploadPath) throws IOException {
        // uploadPath: F:/Lecture/spring-practice/file-util-practice/upload
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        // dirname: F:/Lecture/spring-practice/file-util-practice/upload/2020/07/02
        String dirname = getCurrentUploadPath(uploadPath);
        File target = new File(dirname, filename);
        FileCopyUtils.copy(file.getBytes(), target);
        return dirname + File.separator + filename;
    }

    // 경로가 있으면 경로를 돌려주고 없으면 경로를 만들어서 리턴한다.
    public static String getCurrentUploadPath(String uploadRootPath) {
        Calendar cal = Calendar.getInstance(); // LocalDateTime 과 다르게 싱글톤으로 사용 가능.
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DATE);

        return makeDir(uploadRootPath, "" + y, len2(m), len2(d));
    }

    private static String len2(int n) {
        return new DecimalFormat("00").format(n); // 숫자가 1자리여도 2자리로 변환하기
    }

    private static String makeDir(String uploadRootPath, String... paths) { // String 여러개를 인자로 받자, 2020, 07, 01
        for (String path : paths) {
            uploadRootPath += File.separator + path; // File.seperator: OS 맞는 구분자
        }
        File file = new File(uploadRootPath);
        if(!file.exists()) {
            file.mkdirs(); // 파일이 존재하지 않는다면 상위 디렉토리까지 전부 만든다.
        }
        return uploadRootPath;
    }

}