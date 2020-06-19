package me.hjeong.ajax.jamong;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
