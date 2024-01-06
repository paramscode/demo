package com.paramsandhu.demo.service;

import com.paramsandhu.demo.config.S3Config;
import com.paramsandhu.demo.entity.Employee;
import com.paramsandhu.demo.entity.EmployeeStaging;
import com.paramsandhu.demo.repository.EmployeeRepository;
import com.paramsandhu.demo.repository.EmployeeStagingRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EmployeeProcessorService {

    @Value("${employee.processing.limit}")
    private int processingLimit;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    S3Client s3Client;

    @Value("${report.bucket-name}")
    private String bucketName;

    private final EmployeeRepository employeeRepository;

    private final EmployeeStagingRepository employeeStagingRepository;

    public EmployeeProcessorService(EmployeeRepository employeeRepository, EmployeeStagingRepository employeeStagingRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeStagingRepository = employeeStagingRepository;
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

    private File createCsvFile(List<EmployeeStaging> employees, String filename) throws IOException {
        File file = new File(filename);
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(file), CSVFormat.DEFAULT.withHeader("Account Number", "Name", "Email", "Phone", "Salary", "Tax Due"))) {
            for (EmployeeStaging employee : employees) {
                printer.printRecord(employee.getAccountNumber(), employee.getName(), "","", employee.getSalary(), employee.getTaxDue());
            }
        }
        return file;
    }

    @Transactional
    public void generateAndUploadReport() throws IOException {
        List<EmployeeStaging> employees = employeeStagingRepository.findAllByOrderByCreatedTimestampAsc();
        if (employees.isEmpty())
            return;

        String filename = "employee_report_" + System.currentTimeMillis() + ".txt";

        File file = new File(filename);
        List<Long> processedEmployeeIds = new ArrayList<>();
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(file), CSVFormat.DEFAULT.withHeader("Account Number", "Name", "Email", "Phone", "Salary", "Tax Due"))) {
            for (EmployeeStaging employee : employees) {
                printer.printRecord(employee.getAccountNumber(), employee.getName(), "","", employee.getSalary(), employee.getTaxDue());
                processedEmployeeIds.add(employee.getId());
            }
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build();

        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromFile(file));

        if (!processedEmployeeIds.isEmpty()) {
            employeeStagingRepository.deleteAllByIdIn(processedEmployeeIds);
        }
    }

}

