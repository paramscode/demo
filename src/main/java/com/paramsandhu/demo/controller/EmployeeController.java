package com.paramsandhu.demo.controller;

import com.paramsandhu.demo.dto.EmployeeRequest;
import com.paramsandhu.demo.entity.Employee;
import com.paramsandhu.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @GetMapping
    public ResponseEntity<Iterable<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    @GetMapping("/page/{pageNo}/{pageSize}")
    public ResponseEntity<Iterable<Employee>> getAllEmployeesWithPagination(
            @PathVariable("pageNo") Integer pageNo,
            @PathVariable("pageSize") Integer pageSize) {

        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<Employee> page = employeeRepository.findAll(pageable);
        return ResponseEntity.ok(page);
        //return ResponseEntity.ok(employeeRepository.findAllWithPagination(pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(employeeRepository.findById(id).get());
    }
}
