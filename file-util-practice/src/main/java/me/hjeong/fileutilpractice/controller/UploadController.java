package me.hjeong.fileutilpractice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hjeong.fileutilpractice.config.AppProperties;
import me.hjeong.fileutilpractice.util.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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

    // 이미지 파일이면 썸네일을 보여주고, 이미지 파일이 아니라면 다운로드 링크를 제공한다.
    // http://localhost:8080/displayFile?filename=/2020/07/01/...jpg
    // @RequestParam String filename: "/2020/07/01/...jpg"
    @ResponseBody
    @GetMapping("/displayFile")
    public ResponseEntity<byte[]> displayFile(String fileName) throws IOException { // 파일 내용을 읽을 것! -> byte[]
        log.info("download File .......... fileName={}", fileName); // "/2020/07/01/...jpg"

        InputStream in = null; // 파일을 읽어오자.
        ResponseEntity<byte[]> entity = null; // 파일을 출력하자. 파일을 뷰에 보여줄 때 파일 내용을 바이트 배열로 출력한다.

        try {
            // 파일이 존재하지 않는다면 Not_Found 응답
            File file = new File(appProperties.getUploadRootPath() + fileName);
            log.info("exists={}", file.exists());
            if(!file.exists())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            // 다운로드 할 파일을 읽어오자. FileInputStream은 파일을 쪽 빨아먹을 스트림을 파일에 꽂는 것
            // OutputStream 은 훅 내보낼 내용. 내용을 출력할 때 사용한다.
            in = new FileInputStream(file);

            String formatName = FileUtils.getFileExtension(fileName);
            MediaType mType = FileUtils.getMediaType(formatName);
            HttpHeaders headers = new HttpHeaders();

            // 이미지 파일이라면
            if (mType != null) {
                headers.setContentType(mType); // MediaType.IMAGE_PNG 종류들

                // 이미지 파일이 아니라면
            } else {
                fileName = fileName.substring(fileName.indexOf("_") + 1);
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // download

                // upload (POST Ajax) 할때 파일 이름을 3바이트로 (UTF-8) 로 인코딩했다.
                // 따라서 파일 이름을 받아서 다시 UTF-8 로 쪼개어 바이트로 변환 후에,
                // 자바가 알 수 있도록 자바(브라우저도 이거 씀)가 사용하는 문자열 charset 인 IOS-8859-1 (2바이트) 로 다시 합쳐 스트링으로 변환한다.
                // 2(fileName-produced UTF-8) -> 1(getBytes("UTF-8")) -> 3(new String(..., "ISO-8859-1"), 이 문자열을 브라우저나 자바가 읽을 수 있다)

                // 한글 파일명을 가진 파일을 다시 다운로드 받을 때 깨지지 않게 하기 위한 작업임
                // "/2020/07/01/...txt"
                String dsp = new String(fileName.getBytes("UTF-8"),  "ISO-8859-1");

                // Content-Disposition 이라는 헤더에 attachment 가 붙으면 body 의 내용을 다운로드 받으라는 뜻
                headers.add("Content-Disposition", "attachment; filename=\"" + dsp + "\"");
            }

            // InputStream 을 ByteArray 로 변환하여 응답 body 에 실어서 보낸다.
            // (1,2,3) = (편지 내용, 편지 종류, 응답 코드)
            return new ResponseEntity<>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);

        } catch (Exception e) {
            log.info("error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } finally {
            if (in != null) {
                in.close(); // 반드시 스트림은 닫아줘야 한다. 안그러면 계속 누군가 써버리는 등의 일이 생길 수 있다.
            }
        }
    }

}