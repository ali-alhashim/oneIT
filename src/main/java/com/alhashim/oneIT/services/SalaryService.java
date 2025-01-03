package com.alhashim.oneIT.services;

import com.alhashim.oneIT.dto.BenefitDto;
import com.alhashim.oneIT.models.Salary;
import com.alhashim.oneIT.models.SalaryLine;
import com.alhashim.oneIT.repositories.SalaryLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalaryService {

    @Autowired
    SalaryLineRepository salaryLineRepository;

    // Calculate basic salary by filtering "% Basic %"
    public BigDecimal getBasicSalaryForSalary(Salary salary) {
        List<SalaryLine> salaryLines = salaryLineRepository.findBySalary(salary);

        return salaryLines.stream()
                .filter(salaryLine -> salaryLine.getPayDescription().contains("Basic"))
                .map(SalaryLine::getToPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // Sum up all basic salary lines
    }

    // Fetch all benefits excluding basic salary
    public List<BenefitDto> getBenefitsForSalary(Salary salary) {
        List<SalaryLine> salaryLines = salaryLineRepository.findBySalary(salary);

        return salaryLines.stream()
                .filter(salaryLine -> !salaryLine.getPayDescription().contains("Basic"))  // Exclude basic
                .map(salaryLine -> new BenefitDto(
                        salaryLine.getPayDescription(),
                        salaryLine.getToPay(),
                        salaryLine.getToDeduct()
                ))
                .collect(Collectors.toList());
    }
}
