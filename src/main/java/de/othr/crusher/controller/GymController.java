package de.othr.crusher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin/gyms")
public class GymController {

    @GetMapping
    public String showUserList(Model model) {
        return "pages/admin/gyms";
    }
}
