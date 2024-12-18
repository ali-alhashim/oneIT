package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.EmployeeClearance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeClearanceRepository extends JpaRepository<EmployeeClearance, Long> {

    EmployeeClearance findByEmployee(Employee employee);
}
