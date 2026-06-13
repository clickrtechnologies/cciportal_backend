package com.moov.niger.cciportal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Author Name: Raj Kumar
 * IDE: IntelliJ IDEA Ultimate Edition
 * JDK: 17 version
 * Date: 13-06-2026
 */
@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public  String test(){
        return "testing api";

    }


}
