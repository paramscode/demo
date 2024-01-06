package com.paramsandhu.demo.repository;

import com.paramsandhu.demo.entity.Employee;
import com.paramsandhu.demo.entity.EmployeeStaging;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeStagingRepository extends JpaRepository<EmployeeStaging, Long> {
    // Custom methods if required
    Page<EmployeeStaging> findAll(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EmployeeStaging e where e.isEnriched = false order by e.id ")
    List<EmployeeStaging> findTopNByIsProcessedFalse(Pageable pageable);

    List<EmployeeStaging> findAllByOrderByCreatedTimestampAsc();

    void deleteAllByIdIn(List<Long> ids);
}

