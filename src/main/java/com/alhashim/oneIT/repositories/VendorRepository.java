package com.alhashim.oneIT.repositories;


import com.alhashim.oneIT.models.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Optional<Vendor> findByName(String name);



    @Query("SELECT e FROM Vendor e WHERE " +
            "LOWER(e.registrationNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.arName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.taxNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.iban) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Vendor> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
