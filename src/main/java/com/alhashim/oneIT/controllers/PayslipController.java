package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.dto.PayslipDto;
import com.alhashim.oneIT.models.Department;
import com.alhashim.oneIT.models.Payslip;
import com.alhashim.oneIT.repositories.DepartmentRepository;
import com.alhashim.oneIT.repositories.PayslipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/payslip")
public class PayslipController {

    @Autowired
    PayslipRepository payslipRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @GetMapping("/list")
    public String payslipList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<Payslip> payslipPage;
        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            payslipPage = payslipRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all  with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            payslipPage = payslipRepository.findAll(pageable);
        }


        model.addAttribute("payslips", payslipPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", payslipPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", payslipPage.getTotalElements());
        model.addAttribute("pageTitle","Payslip List");
        return "/payslip/list";
    }


    @GetMapping("/add")
    public String addPayslip(Model model)
    {
        PayslipDto payslipDto = new PayslipDto();

        List<Department> departments = departmentRepository.findAll();

        model.addAttribute("departments", departments);
        model.addAttribute("payslipDto", payslipDto);
        return "/payslip/add";
    }
}
