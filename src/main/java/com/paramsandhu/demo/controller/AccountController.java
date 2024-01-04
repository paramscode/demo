package com.paramsandhu.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    //endpoint which will return a random account number for the given employee id
    @GetMapping("/{id}")
    public String getAccountNumber() {
        //return a random guid for now
        return java.util.UUID.randomUUID().toString();
    }
}
