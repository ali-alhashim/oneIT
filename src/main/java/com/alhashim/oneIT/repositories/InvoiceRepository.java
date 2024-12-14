package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Invoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


    @Query("SELECT e FROM Invoice e WHERE " +
            "LOWER(e.invoiceNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.status) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.paymentMethod) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.vendor.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.purchaseOrder.code) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Invoice> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
