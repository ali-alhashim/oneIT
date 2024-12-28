package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.EmployeeCalendar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmployeeCalendarRepository extends JpaRepository<EmployeeCalendar, Long> {

    @Query("SELECT ec FROM EmployeeCalendar ec WHERE ec.employee = :employee AND ec.dayDate >= :start AND ec.dayDate <= :end")
    Page<EmployeeCalendar> findByEmployeeFromTo(@Param("employee") Employee employee,
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end, Pageable pageable);


    @Query("SELECT ec FROM EmployeeCalendar ec WHERE ec.employee = :employee AND ec.dayDate =:dayDate")
    Optional<EmployeeCalendar> findByDayDateAndEmployee(@Param("employee") Employee employee, @Param("dayDate") LocalDate dayDate);

    Page<EmployeeCalendar> findByEmployee(Employee employee, Pageable pageable);




    @Query("SELECT e FROM EmployeeCalendar e WHERE " +
            "LOWER(e.employee.badgeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.arName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.workMobile) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.workEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<EmployeeCalendar> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
