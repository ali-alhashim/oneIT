package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
