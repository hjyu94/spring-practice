package me.hjeong.ajax.jamong;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

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
}
