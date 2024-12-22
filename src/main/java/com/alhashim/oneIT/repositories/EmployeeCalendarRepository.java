package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.EmployeeCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmployeeCalendarRepository extends JpaRepository<EmployeeCalendar, Long> {

    @Query("SELECT ec FROM EmployeeCalendar ec WHERE ec.employee = :employee AND ec.dayDate >= :start AND ec.dayDate <= :end")
    List<EmployeeCalendar> findByEmployeeFromTo(@Param("employee") Employee employee,
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end);


    @Query("SELECT ec FROM EmployeeCalendar ec WHERE ec.employee = :employee AND ec.dayDate =:dayDate")
    Optional<EmployeeCalendar> findByDayDateAndEmployee(@Param("employee") Employee employee, @Param("dayDate") LocalDate dayDate);

}
