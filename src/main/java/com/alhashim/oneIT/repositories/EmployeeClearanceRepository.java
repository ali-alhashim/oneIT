package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.EmployeeClearance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeClearanceRepository extends JpaRepository<EmployeeClearance, Long> {

    EmployeeClearance findByEmployee(Employee employee);

    @Query("SELECT e FROM EmployeeClearance e WHERE " +
            "LOWER(e.employee.badgeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.arName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.workMobile) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.workEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<EmployeeClearance> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
