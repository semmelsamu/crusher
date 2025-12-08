package de.othr.crusher;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class HomeController {

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String readersBooks(RedirectAttributes redirectAttributes) {
        return "pages/home";
    }

    @RequestMapping(value="/te", method= RequestMethod.GET)
    public String te(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "message", "Hello!",
            "type", "success",
            "title", "Success"
        ));
        
        return "redirect:/";
    }
    
    @RequestMapping(value="/components", method= RequestMethod.GET)
    public String components() {
        return "pages/components";
    }
}
