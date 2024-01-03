package com.paramsandhu.demo.repository;

import com.paramsandhu.demo.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Custom methods if required
    Page<Employee> findAll(Pageable pageable);
}

