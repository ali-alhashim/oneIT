package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface  EmployeeRepository  extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e JOIN FETCH e.roles WHERE e.badgeNumber = :badgeNumber")
     Optional<Employee>  findByBadgeNumber(String badgeNumber);

    @Query("SELECT e.badgeNumber FROM Employee e ORDER BY e.badgeNumber DESC LIMIT 1")
    String findLastBadgeNumber();




    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.badgeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.arName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.workMobile) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.workEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Employee> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
