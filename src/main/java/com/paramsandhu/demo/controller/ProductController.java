package com.paramsandhu.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ProductController {
    @GetMapping("/hello")
    public String sayHello(
            @RequestParam("name") String name) {
        log.info("inside");
        return String.format("Hello %s!", name);
    }

}
