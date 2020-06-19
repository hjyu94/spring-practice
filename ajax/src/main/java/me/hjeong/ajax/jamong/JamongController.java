package me.hjeong.ajax.jamong;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class JamongController {

    @GetMapping("/jamong")
    public String home() {
        return "jamong.html";
    }

    @RequestMapping(value="/requestObject", method= RequestMethod.POST)
    @ResponseBody
    public String simpleWithObject(Jamong jamong) {
        return jamong.getName() + jamong.getAge();
    }

    @RequestMapping(value="/serialize", method=RequestMethod.POST)
    @ResponseBody
    public String serialize(Jamong jamong) {
        return jamong.getName() + jamong.getAge();
    }

    @RequestMapping(value="/stringify", method=RequestMethod.POST)
    @ResponseBody
    public Object stringify(@RequestBody Jamong jamong) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", jamong.getName());
        map.put("age", jamong.getAge());
        return map;
    }

    @RestController
    public class AjaxRestController {
        @PostMapping("/restController")
        public Object restController(@RequestBody Jamong jamong) {
            ArrayList<String> arrList = new ArrayList<String>();
            for(int i=0; i<5; i++) {
                arrList.add(jamong.getName() + i);
            }
            return arrList;
        }
    }

    @RequestMapping(value="/file", method=RequestMethod.POST)
    @ResponseBody
    public Object file(AjaxFile file) {
        List<MultipartFile> list = file.getImages();
        ArrayList<String> fileNameList = new ArrayList<String>();
        for(MultipartFile mf : list) {
            fileNameList.add(mf.getOriginalFilename());
        }
        return fileNameList;
    }
}
