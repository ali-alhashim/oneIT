package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.models.EmployeeClearance;
import com.alhashim.oneIT.repositories.EmployeeClearanceRepository;
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

@Controller
@RequestMapping("/clearance")
public class ClearanceController {

    @Autowired
    EmployeeClearanceRepository employeeClearanceRepository;

    @GetMapping("/list")
    public String clearanceList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<EmployeeClearance> employeeClearancePage;
        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            employeeClearancePage = employeeClearanceRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            employeeClearancePage = employeeClearanceRepository.findAll(pageable);
        }

        model.addAttribute("clearances", employeeClearancePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeeClearancePage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", employeeClearancePage.getTotalElements());
        model.addAttribute("pageTitle","Clearance List");

        return "/clearance/list";
    }
}
