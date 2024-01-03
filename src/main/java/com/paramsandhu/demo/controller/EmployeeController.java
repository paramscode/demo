package com.paramsandhu.demo.controller;

import com.paramsandhu.demo.dto.EmployeeRequest;
import com.paramsandhu.demo.entity.Employee;
import com.paramsandhu.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping
    public ResponseEntity<String> createEmployee(@RequestBody EmployeeRequest employeeRequest) {
        employeeRepository.save(employeeRequest.getEmployee());
        return ResponseEntity.ok("Employee saved successfully");
    }
}
