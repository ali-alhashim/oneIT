package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    List<SystemLog> findByEmployee(Employee employee);

    Page<SystemLog> findByEmployeeId(Long employeeId, Pageable pageable);
}
