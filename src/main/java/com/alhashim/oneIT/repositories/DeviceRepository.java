package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Department;
import com.alhashim.oneIT.models.Device;
import com.alhashim.oneIT.models.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findBySerialNumber(String serialNumber);

    @Query("SELECT e FROM Device e WHERE " +
            "LOWER(e.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.manufacture) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Device> findByKeyword(@Param("keyword") String keyword, Pageable pageable);


    //return device under department with search for device category or device serial number
    @Query("SELECT t FROM Device t " +
            "WHERE t.user.department = :department " +
            "AND (LOWER(t.category) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Device> findByKeywordAndDepartment(@Param("keyword") String keyword,
                                            @Param("department") Department department,
                                            Pageable pageable);

    // return device under department
    Page<Device> findByUser_Department(Department department, Pageable pageable);

    // return device under user
    Page<Device> findByUser(Employee employee, Pageable pageable);

    // return device under user with search
    @Query("SELECT t FROM Device t " +
            "WHERE t.user = :user " +
            "AND (LOWER(t.category) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Device> findByKeywordAndUser(@Param("keyword") String keyword,
                                            @Param("user") Employee user,
                                            Pageable pageable);

}
