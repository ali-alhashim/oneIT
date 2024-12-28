package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.EmployeeCalendar;
import com.alhashim.oneIT.repositories.EmployeeCalendarRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
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

import java.time.LocalDate;

@Controller
@RequestMapping("/timesheet")
public class TimesheetController {

    @Autowired
    EmployeeCalendarRepository employeeCalendarRepository;

    @Autowired
    EmployeeRepository employeeRepository;


    // only for HR & admin
    @GetMapping("/list")
    public String timesheetList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<EmployeeCalendar> employeeCalendarPage;

        //for search
        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            employeeCalendarPage = employeeCalendarRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all employees with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            employeeCalendarPage = employeeCalendarRepository.findAll(pageable);
        }

        model.addAttribute("timesheets", employeeCalendarPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeeCalendarPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", employeeCalendarPage.getTotalElements());
        model.addAttribute("pageTitle","Timesheet List");

        // ---
        return "/timesheet/list";
    }

    // only for HR and admin
    @GetMapping("/detail")
    public String timesheetDetail(Model model, @RequestParam Long id)
    {
        EmployeeCalendar employeeCalendar = employeeCalendarRepository.findById(id).orElse(null);
        model.addAttribute("timesheet", employeeCalendar);
        return "/timesheet/detail";
    }


    //Only for him self
    @GetMapping("/employee")
    public String employeeTimesheet(@RequestParam Long id, Model model, @RequestParam(required = false)LocalDate startDate, @RequestParam(required = false) LocalDate endDate, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if(employee == null)
        {
            return "/404";
        }

        Page<EmployeeCalendar> employeeCalendarPage;

        if(startDate != null)
        {
            System.out.println("Filter Timesheet From: "+startDate +" To: "+endDate);
            employeeCalendarPage = employeeCalendarRepository.findByEmployeeFromTo(employee, startDate, endDate, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")) );
        }
        else
        {
            employeeCalendarPage = employeeCalendarRepository.findByEmployee(employee, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }

        model.addAttribute("employeeId", employee.getId());
        model.addAttribute("timesheets", employeeCalendarPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeeCalendarPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", employeeCalendarPage.getTotalElements());
        model.addAttribute("pageTitle","Timesheet Employee List");

        return "/timesheet/employeeTimesheet";
    }

}
