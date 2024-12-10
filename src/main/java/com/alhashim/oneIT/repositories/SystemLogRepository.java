package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    List<SystemLog> findByEmployee(Employee employee);

    Page<SystemLog> findByEmployeeId(Long employeeId, Pageable pageable);


    @Query("SELECT e FROM SystemLog e WHERE " +
            "LOWER(e.employee.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.badgeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.arName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SystemLog> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
