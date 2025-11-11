package edu.oth.crusher.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/test")
public class ThymeleafTestController {

    @GetMapping("/thymeleaf")
    public String thymeleafTest(Model model) {
        // Add various types of model attributes to demonstrate Thymeleaf features
        model.addAttribute("message", "Welcome to Thymeleaf Test Page");
        model.addAttribute("username", "John Doe");
        model.addAttribute("age", 25);
        model.addAttribute("score", 95.5);
        model.addAttribute("isActive", true);
        model.addAttribute("isPremium", false);
        
        // Add a list to demonstrate th:each
        List<String> hobbies = Arrays.asList("Rock Climbing", "Hiking", "Photography", "Reading");
        model.addAttribute("hobbies", hobbies);
        
        // Add some data for arithmetic expressions
        model.addAttribute("price", 100);
        model.addAttribute("quantity", 3);
        
        return "thymeleaf-test";
    }
}

