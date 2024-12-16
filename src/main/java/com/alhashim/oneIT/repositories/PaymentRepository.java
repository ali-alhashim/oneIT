package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
