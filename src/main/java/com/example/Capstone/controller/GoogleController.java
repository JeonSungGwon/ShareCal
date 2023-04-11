package com.example.Capstone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GoogleController {
    @GetMapping("google/goocal")
    public String goocal(Model model){
        return "google/goocal";
    }

    @GetMapping("google/receiveAC")
    public String receiveAC(@RequestParam("code") String code, Model model){
        model.addAttribute("code",code);
        return "google/receiveAC";
    }
}

