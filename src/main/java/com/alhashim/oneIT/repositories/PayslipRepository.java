package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Payslip;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    @Query("SELECT e FROM Payslip e WHERE " +
            "LOWER(e.codeName) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    Page<Payslip> findByKeyword(@Param("keyword") String keyword, Pageable pageable);



    @Query("SELECT e FROM Payslip e WHERE e.employee = :employee " +
            "AND LOWER(e.codeName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Payslip> findByKeywordAndEmployee(@Param("keyword") String keyword,
                                           @Param("employee") Employee employee,
                                           Pageable pageable);


    @Query("SELECT e FROM Payslip e WHERE e.employee = :employee ")
    Page<Payslip> findAllForEmployee(@Param("employee") Employee employee,Pageable pageable);
}
