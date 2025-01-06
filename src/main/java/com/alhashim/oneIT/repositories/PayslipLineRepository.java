package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Payslip;
import com.alhashim.oneIT.models.PayslipLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayslipLineRepository extends JpaRepository<PayslipLine, Long> {

    List<PayslipLine> findByPayslip(Payslip payslip);
}
