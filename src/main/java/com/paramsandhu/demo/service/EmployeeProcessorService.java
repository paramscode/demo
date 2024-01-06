package com.paramsandhu.demo.service;

import com.paramsandhu.demo.entity.Employee;
import com.paramsandhu.demo.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class EmployeeProcessorService {

    @Value("${employee.processing.limit}")
    private int processingLimit;

    @Autowired
    private RestTemplate restTemplate;

    private final EmployeeRepository employeeRepository;

    public EmployeeProcessorService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    //@Scheduled(cron = "*/30 * * * * *") // Runs every 10 seconds
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

    public double getTaxDue(long employeeId) {
        try {
            String url = "http://localhost:8080/api/tax/" + employeeId;
            return restTemplate.getForObject(url, Double.class);
        } catch (HttpClientErrorException e) {
            // Handle HTTP errors here
            return 0.0;
        }
    }

    public String getAccountNumber(long employeeId) {
        try {
            String url = "http://localhost:8080/api/account/" + employeeId;
            return restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e) {
            // Handle HTTP errors here
            return "";
        }
    }
}

