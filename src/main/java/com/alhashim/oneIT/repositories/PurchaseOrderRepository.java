package com.alhashim.oneIT.repositories;


import com.alhashim.oneIT.models.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @Query("SELECT e FROM PurchaseOrder e WHERE " +
            "LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.vendor.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(e.totalPriceWithVAT AS string) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.status) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<PurchaseOrder> findByKeyword(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT e.code FROM PurchaseOrder e ORDER BY e.code DESC LIMIT 1")
    String findLastCode();
}
