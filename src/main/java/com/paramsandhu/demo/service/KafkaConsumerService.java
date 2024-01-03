package com.paramsandhu.demo.service;

import com.paramsandhu.demo.dto.EmployeeRequest;
import com.paramsandhu.demo.entity.Employee;
import com.paramsandhu.demo.repository.EmployeeRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaConsumerService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @KafkaListener(topics = "employee")
    public void consume(String message) {
        processMessageAsync(message);
    }

    @Async
    public CompletableFuture<Void> processMessageAsync(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            EmployeeRequest employeeRequest = mapper.readValue(message, EmployeeRequest.class);
            employeeRepository.save(employeeRequest.getEmployee());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }
}

