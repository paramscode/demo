package com.paramsandhu.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paramsandhu.demo.dto.EmployeeRequest;
import com.paramsandhu.demo.entity.Employee;
import com.paramsandhu.demo.entity.EmployeeStaging;
import com.paramsandhu.demo.entity.FalloutRecord;
import com.paramsandhu.demo.repository.EmployeeRepository;
import com.paramsandhu.demo.repository.EmployeeStagingRepository;
import com.paramsandhu.demo.repository.FalloutRepository;
import com.paramsandhu.demo.service.EmployeeProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FalloutRepository falloutRepository;

    @Autowired
    private EmployeeStagingRepository employeeStagingRepository;

    @Autowired
    private EmployeeProcessorService employeeProcessorService;

    @PostMapping
    @CacheEvict(value = "employees", allEntries = true)
    public ResponseEntity<Employee> createEmployee(@RequestBody String employeeRequest) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(employeeRequest);
        } catch (JsonProcessingException e) {
            FalloutRecord falloutRecords = new FalloutRecord();
            falloutRecords.setPayload(employeeRequest);
            falloutRepository.save(falloutRecords);
            return ResponseEntity.badRequest().build();
        }
        JsonNode employeeNode = rootNode.path("employee");

        Employee employee = new Employee();
        employee.setName(employeeNode.path("name").asText());
        employee.setSalary(employeeNode.path("salary").asDouble());
        employee.setMarried(employeeNode.path("married").asBoolean());
        employee.setPayload(employeeNode.toString());
        Employee savedEmployee = employeeRepository.save(employee);

        EmployeeStaging employeeStaging = new EmployeeStaging();
        employeeStaging.setName(employeeNode.path("name").asText());
        employeeStaging.setSalary(employeeNode.path("salary").asDouble());
        employeeStaging.setMarried(employeeNode.path("married").asBoolean());
        employeeStaging.setEmployeeId(savedEmployee.getId());
        employeeStaging.setTaxDue(employeeProcessorService.getTaxDue(savedEmployee.getId()));
        employeeStaging.setAccountNumber(employeeProcessorService.getAccountNumber(savedEmployee.getId()));
        employeeStagingRepository.save(employeeStaging);
        return ResponseEntity.ok(savedEmployee);
    }


    /*@PostMapping
    @CacheEvict(value = "employees", allEntries = true)
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeRequest employeeRequest) {
        Employee savedEmployee = employeeRepository.save(employeeRequest.getEmployee());
        return ResponseEntity.ok(savedEmployee);
    }*/

    @GetMapping
    @Cacheable("employees")
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

    //endpoint which takes an employee object
}
