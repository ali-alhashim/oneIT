package com.alhashim.oneIT.repositories;


import com.alhashim.oneIT.models.Department;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByStatus(String status);



    @Query("SELECT e FROM Request e WHERE " +
            "LOWER(e.requestedBy.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.requestedBy.badgeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.status) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.justification) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Request> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    //return all request for employee
    Page<Request> findByRequestedBy(Employee employee, Pageable pageable);

    //return all request for department
    Page<Request> findByRequestedBy_Department(Department department, Pageable pageable);


    @Query("SELECT t FROM Request t " +
            "WHERE t.requestedBy.department = :department " +
            "AND (LOWER(t.category) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.requestedBy.badgeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Request> findByKeywordAndDepartment(@Param("keyword") String keyword,
                                             @Param("department") Department department,
                                             Pageable pageable);



    @Query("SELECT t FROM Request t " +
            "WHERE t.requestedBy = :employee " +
            "AND (LOWER(t.category) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.requestedBy.badgeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Request> findByKeywordAndRequestedBy(@Param("keyword") String keyword,
                                              @Param("employee") Employee employee,
                                              Pageable pageable);

}
