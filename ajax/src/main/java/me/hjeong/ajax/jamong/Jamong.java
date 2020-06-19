package me.hjeong.ajax.jamong;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter @Getter
public class Jamong {
    String name;
    int age;
}

@Getter @Setter
class AjaxFile {
    List<MultipartFile> images;
}