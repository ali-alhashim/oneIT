package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.dto.ShiftScheduleDto;
import com.alhashim.oneIT.models.Department;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.ShiftSchedule;
import com.alhashim.oneIT.models.SystemLog;
import com.alhashim.oneIT.repositories.DepartmentRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.ShiftScheduleRepository;
import com.alhashim.oneIT.repositories.SystemLogRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/shiftSchedule")
public class ShiftScheduleController {

    @Autowired
    ShiftScheduleRepository shiftScheduleRepository;


    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    SystemLogRepository systemLogRepository;

    @GetMapping("/list")
public String shiftScheduleList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<ShiftSchedule> shiftSchedulePage;

        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            shiftSchedulePage = shiftScheduleRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all  with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            shiftSchedulePage = shiftScheduleRepository.findAll(pageable);
        }


        model.addAttribute("shiftSchedules", shiftSchedulePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", shiftSchedulePage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", shiftSchedulePage.getTotalElements());
        model.addAttribute("pageTitle","shiftSchedules List");

        return "/shiftSchedule/list";
    } // GET list


    @GetMapping("/add")
    public String addShiftSchedule(Model model)
    {
        ShiftScheduleDto shiftScheduleDto = new ShiftScheduleDto();

        model.addAttribute("shiftScheduleDto", shiftScheduleDto);
        return "/shiftSchedule/add";
    }

    @PostMapping("/add")
    public String addShiftScheduleDo(@Valid @ModelAttribute ShiftScheduleDto shiftScheduleDto, BindingResult result,Model model)
    {
        if(result.hasErrors())
        {
            model.addAttribute("message", result.getAllErrors());
            return "/404";
        }
        ShiftSchedule shiftSchedule = new ShiftSchedule();
        shiftSchedule.setName(shiftScheduleDto.getName());
        shiftSchedule.setStartTime(shiftScheduleDto.getStartTime());
        shiftSchedule.setEndTime(shiftScheduleDto.getEndTime());
        shiftSchedule.setSundayWork(shiftScheduleDto.getSundayWork());
        shiftSchedule.setMondayWork(shiftScheduleDto.getMondayWork());
        shiftSchedule.setTuesdayWork(shiftScheduleDto.getTuesdayWork());
        shiftSchedule.setWednesdayWork(shiftScheduleDto.getWednesdayWork());
        shiftSchedule.setThursdayWork(shiftScheduleDto.getThursdayWork());
        shiftSchedule.setFridayWork(shiftScheduleDto.getFridayWork());
        shiftSchedule.setSaturdayWork(shiftScheduleDto.getSaturdayWork());

        shiftScheduleRepository.save(shiftSchedule);


        // Log the  action
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        SystemLog systemLog = new SystemLog();
        systemLog.setCreatedAt(LocalDateTime.now());
        systemLog.setEmployee(currentUser);
        systemLog.setDescription("Add shiftSchedule   : "+shiftSchedule.getName());
        systemLogRepository.save(systemLog);

        return "redirect:/shiftSchedule/list";
    }


    @GetMapping("/shiftScheduleDetail")
    public String shiftScheduleDetail(@RequestParam Long id, Model model)
    {
        ShiftSchedule shiftSchedule = shiftScheduleRepository.findById(id).orElse(null);
        if(shiftSchedule ==null)
        {
            return "/404";
        }

        List<Employee> employees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<Department> departments = departmentRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        model.addAttribute("employees", employees);
        model.addAttribute("departments", departments);
        model.addAttribute("shiftSchedule", shiftSchedule);
        return "/shiftSchedule/detail";
    }


    @PostMapping("/add-employee")
    public  String addEmployee(@RequestParam Long shiftScheduleId, @RequestParam String badgeNumber)
    {
        ShiftSchedule shiftSchedule = shiftScheduleRepository.findById(shiftScheduleId).orElse(null);
        if(shiftSchedule ==null)
        {
            return "/404";
        }
        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber).orElse(null);
        if(employee ==null)
        {
            return "/404";
        }

        employee.setShiftSchedule(shiftSchedule);
        employeeRepository.save(employee);
        return "redirect:/shiftSchedule/shiftScheduleDetail?id="+shiftScheduleId;
    }


    @PostMapping("/add-department")
    public String addDepartment(@RequestParam Long shiftScheduleId, @RequestParam Long departmentId)
    {
        ShiftSchedule shiftSchedule = shiftScheduleRepository.findById(shiftScheduleId).orElse(null);
        if(shiftSchedule ==null)
        {
            return "/404";
        }

        Department department  = departmentRepository.findById(departmentId).orElse(null);
        if(department ==null)
        {
            return "/404";
        }

        List<Employee> employees = new ArrayList<>(department.getEmployees());
        employees.forEach(employee -> {
            employee.setShiftSchedule(shiftSchedule);
            employeeRepository.save(employee);
        });

        // Log the  action
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        SystemLog systemLog = new SystemLog();
        systemLog.setCreatedAt(LocalDateTime.now());
        systemLog.setEmployee(currentUser);
        systemLog.setDescription("Add Department : "+department.getName()+" to shiftSchedule   : "+shiftSchedule.getName());
        systemLogRepository.save(systemLog);

        return "redirect:/shiftSchedule/shiftScheduleDetail?id="+shiftScheduleId;
    }

}
