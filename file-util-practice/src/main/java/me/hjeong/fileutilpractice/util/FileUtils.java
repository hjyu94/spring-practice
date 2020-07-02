package me.hjeong.fileutilpractice.util;

import org.imgscalr.Scalr;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileUtils {

    private static Map<String, MediaType> mediaMap;
    static { // 생성자가 없기 때문에, FileUtils 를 new 해서 만들어 쓸 게 아니기 때문에 메모리에 올라올 때 static 일 때 처리한다
        mediaMap = new HashMap<>();
        mediaMap.put("JPG", MediaType.IMAGE_JPEG);
        mediaMap.put("GIF", MediaType.IMAGE_GIF);
        mediaMap.put("PNG", MediaType.IMAGE_PNG);
    }
    public static MediaType getMediaType(String ext) {
        return mediaMap.get(ext.toUpperCase());
    }

    public static String uploadFile(MultipartFile file, String uploadPath) throws IOException {
        // uploadPath: F:/Lecture/spring-practice/file-util-practice/upload
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        // dirname: F:/Lecture/spring-practice/file-util-practice/upload/2020/07/02
        String dirname = getCurrentUploadPath(uploadPath);
        File target = new File(dirname, filename);
        FileCopyUtils.copy(file.getBytes(), target);

        String uploadedFilename = null;
        String ext = getFileExtension(filename); // "png"
        if (getMediaType(ext) != null) // if image?
            uploadedFilename = makeThumbnail(uploadPath, dirname, filename); // "/2020/07/01/s_UUID_cat.png"
        else
            uploadedFilename = makeIcon(uploadPath, dirname, filename); // "/2020/07/UUID_test.html"

        return uploadedFilename;
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

        private static String makeIcon(String uploadPath, String dirname, String filename) {
            String iconName = dirname + filename;
            return iconName.substring(uploadPath.length()).replace(File.separatorChar, '/');
        }

        // 업로드한 파일을 읽어서 세로 100인 썸네일 이미지를 만들어 저장한다.
        public static String makeThumbnail(String uploadRootPath, String dirname, String filename) throws IOException {
            BufferedImage srcImg = ImageIO.read(new File(dirname, filename));
            BufferedImage destImg = Scalr.resize(srcImg, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_HEIGHT, 100);
            // width: FIT_TO_HEIGHT, height: 100

            String thumbnailName = dirname + File.separator + "s_" + filename; // "F:.../upload\2020\07\01" + "\" + "s_" + "UUID_cat.png"
            // 파일명에 . 없으면 ArrayOutOfException 발생
            String ext = getFileExtension(filename); // "png"
            File newFile = new File(thumbnailName);

            ImageIO.write(destImg, ext.toUpperCase(), newFile); // 나중에 mime 체크할 때 편하게 하도록 대문자로 설정
            return thumbnailName.substring(uploadRootPath.length()).replace(File.separatorChar, '/');
        }

        public static String getFileExtension(String filename) {
            return filename.substring(filename.lastIndexOf(".") + 1); // . 이후로 문자열 끝까지 스트링을 잘라온다
        }

    }