package com.paramsandhu.demo.service;

import com.paramsandhu.demo.entity.Employee;
import com.paramsandhu.demo.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class EmployeeProcessorService {

    @Value("${employee.processing.limit}")
    private int processingLimit;

    private final EmployeeRepository employeeRepository;

    public EmployeeProcessorService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Scheduled(cron = "*/30 * * * * *") // Runs every 10 seconds
    @Transactional
    //evict cache
    @CacheEvict(value = "employees", allEntries = true)
    public void processUnprocessedEmployees() {
        log.info("Processing unprocessed employees");
        Pageable limit = PageRequest.of(0, processingLimit);
        List<Employee> employees = employeeRepository.findTopNByIsProcessedFalse(limit);

        for (Employee employee : employees) {
            employee.setName(employee.getName().toUpperCase());
            employee.setProcessed(true);
            employeeRepository.save(employee);
        }
    }
}

