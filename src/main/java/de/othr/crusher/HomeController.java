package de.othr.crusher;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HomeController {

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String readersBooks() {
        return "pages/home";
    }
    
    @RequestMapping(value="/components", method= RequestMethod.GET)
    public String components() {
        return "pages/components";
    }
}
