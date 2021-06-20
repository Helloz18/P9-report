package com.mediscreen.report.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    
    /**
 * Swagger view to test the application
 * @return
 */   
@GetMapping(path = "/swagger")
public String mainPage() {
    return "redirect:/swagger-ui/index.html";
}
}
