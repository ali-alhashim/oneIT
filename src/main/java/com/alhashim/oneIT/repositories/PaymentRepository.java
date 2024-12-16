package com.alhashim.oneIT.repositories;


import com.alhashim.oneIT.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT e FROM Payment e WHERE " +
            "LOWER(e.invoice.invoiceNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.invoice.vendor.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
    )
    Page<Payment> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
