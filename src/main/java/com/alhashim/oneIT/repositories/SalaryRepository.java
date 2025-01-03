package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployee(Employee employee);

    @Query(value = "SELECT * FROM salary s WHERE s.employee_id = :employeeId ORDER BY s.created_at DESC LIMIT 1", nativeQuery = true)
    Salary findLastSalaryForEmployee(@Param("employeeId") Long employeeId);
}
