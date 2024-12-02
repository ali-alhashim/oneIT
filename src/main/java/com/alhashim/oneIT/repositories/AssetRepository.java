package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Asset;
import com.alhashim.oneIT.models.Device;
import com.alhashim.oneIT.models.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query("SELECT DISTINCT a.employee FROM Asset a WHERE a.device.serialNumber = :serialNumber")
    List<Employee> findEmployeesByDeviceSerialNumber(@Param("serialNumber") String serialNumber);

    List<Asset> findByDevice(Device device);

    @Query("SELECT a FROM Asset a WHERE a.device.serialNumber = :serialNumber")
    List<Asset> findAssetsByDeviceSerialNumber(@Param("serialNumber") String serialNumber);


    @Query("SELECT e.code FROM Asset e ORDER BY e.code DESC LIMIT 1")
    String findLastCode();


    Optional<Asset> findByCode(String code);


    @Query("SELECT e FROM Asset e WHERE " +
            "LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.badgeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.device.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.device.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.employee.arName) LIKE LOWER(CONCAT('%', :keyword, '%'))"
            )
    Page<Asset> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}