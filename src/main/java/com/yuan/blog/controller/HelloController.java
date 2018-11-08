package com.yuan.blog.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;

@RestController
@RequestMapping("/yuan")
public class HelloController {

    @GetMapping("/bhu/{no}")
    public String hello(@PathVariable("no") String no){
        String result = "";
        try {
            Document doc= Jsoup.parse(new URL("https://www.zhihu.com/question/"+ no),(3000));
            result = doc.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }
}
