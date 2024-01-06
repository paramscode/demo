package com.paramsandhu.demo.repository;

import com.paramsandhu.demo.entity.Employee;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Custom methods if required
    Page<Employee> findAll(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Employee e where e.isProcessed = false order by e.id ")
    List<Employee> findTopNByIsProcessedFalse(Pageable pageable);


}

