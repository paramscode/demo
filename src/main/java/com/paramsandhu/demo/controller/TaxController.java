package com.paramsandhu.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tax")
public class TaxController {

    @GetMapping("/{id}")
    public double getTax(@PathVariable Long id) {
        //return random tax amount rounded to 2 digits for now
        return Math.round(Math.random() * 10000) / 100.0;

    }

}
