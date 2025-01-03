package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Salary;
import com.alhashim.oneIT.models.SalaryLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryLineRepository extends JpaRepository<SalaryLine, Long> {
    List<SalaryLine> findBySalary(Salary salary);
}
