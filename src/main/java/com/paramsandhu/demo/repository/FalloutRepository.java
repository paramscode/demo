package com.paramsandhu.demo.repository;

import com.paramsandhu.demo.entity.FalloutRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FalloutRepository extends JpaRepository<FalloutRecord, Long> {
    // Custom methods if required
}
