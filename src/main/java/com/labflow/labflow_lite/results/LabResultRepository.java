package com.labflow.labflow_lite.results;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    List<LabResult> findAllByOrderByIdDesc();
}