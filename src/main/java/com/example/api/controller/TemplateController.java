package com.example.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping(path = "/")
public class TemplateController {

    @GetMapping(path = "login")
    public String getLogin()
    {
        return "login";
    }
}
