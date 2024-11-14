package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface  EmployeeRepository  extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e JOIN FETCH e.roles WHERE e.badgeNumber = :badgeNumber")
     Optional<Employee>  findByBadgeNumber(String badgeNumber);
}
