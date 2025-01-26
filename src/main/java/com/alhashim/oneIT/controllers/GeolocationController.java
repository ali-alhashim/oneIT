package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.dto.GeolocationDto;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Geolocation;
import com.alhashim.oneIT.models.SystemLog;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.GeolocationRepository;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/geolocation")
public class GeolocationController {

    @Autowired
    GeolocationRepository geolocationRepository;

    @Autowired
    SystemLogRepository systemLogRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @GetMapping("/list")
    public String geolocationList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<Geolocation> geolocationPage;

        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            geolocationPage = geolocationRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all employees with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            geolocationPage = geolocationRepository.findAll(pageable);
        }

        model.addAttribute("geolocations", geolocationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", geolocationPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", geolocationPage.getTotalElements());
        model.addAttribute("pageTitle","Geolocation List");

        return "/geolocation/list";
    } // GET list



    @GetMapping("/add")
    public String addGeolocation(Model model)
    {
        GeolocationDto geolocationDto = new GeolocationDto();

        model.addAttribute("geolocationDto", geolocationDto);
        return "/geolocation/add";
    }

    @PostMapping("/add")
    public String addGeolocationDo(@Valid @ModelAttribute GeolocationDto geolocationDto)
    {
        Geolocation geolocation = new Geolocation();
        geolocation.setAreaName(geolocationDto.getAreaName());
        geolocation.setCreatedAt(LocalDateTime.now());

        geolocation.setLatitudeA(geolocationDto.getLatitudeA());
        geolocation.setLatitudeB(geolocationDto.getLatitudeB());
        geolocation.setLatitudeC(geolocationDto.getLatitudeC());
        geolocation.setLatitudeD(geolocationDto.getLatitudeD());

        geolocation.setLongitudeA(geolocationDto.getLongitudeA());
        geolocation.setLongitudeB(geolocationDto.getLongitudeB());
        geolocation.setLongitudeC(geolocationDto.getLongitudeC());
        geolocation.setLongitudeD(geolocationDto.getLongitudeD());

        geolocationRepository.save(geolocation);


        // Log the  action
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        SystemLog systemLog = new SystemLog();
        systemLog.setCreatedAt(LocalDateTime.now());
        systemLog.setEmployee(currentUser);
        systemLog.setDescription("Add Geolocation name:"+geolocation.getAreaName());
        systemLogRepository.save(systemLog);

        return "redirect:/geolocation/list";
    } // Add Geolocation


    @GetMapping("/geolocationDetail")
    public String geolocationDetail(@RequestParam Long id, Model model)
    {
        Geolocation geolocation = geolocationRepository.findById(id).orElse(null);
        if(geolocation ==null)
        {
            return "/404";
        }

        model.addAttribute("geolocation", geolocation);
        return "/geolocation/detail";
    }

    @GetMapping("/edit")
    public String geolocationEdit(@RequestParam Long id, Model model)
    {
        Geolocation geolocation = geolocationRepository.findById(id).orElse(null);
        if(geolocation ==null)
        {
            return "/404";
        }

        GeolocationDto geolocationDto = new GeolocationDto();

        geolocationDto.setId(geolocation.getId());

        geolocationDto.setAreaName(geolocation.getAreaName());

        geolocationDto.setLatitudeA(geolocation.getLatitudeA());
        geolocationDto.setLongitudeA(geolocation.getLongitudeA());

        geolocationDto.setLatitudeB(geolocation.getLatitudeB());
        geolocationDto.setLongitudeB(geolocation.getLongitudeB());

        geolocationDto.setLatitudeC(geolocation.getLatitudeC());
        geolocationDto.setLongitudeC(geolocation.getLongitudeC());

        geolocationDto.setLatitudeD(geolocation.getLatitudeD());
        geolocationDto.setLongitudeD(geolocation.getLongitudeD());



        model.addAttribute("geolocationDto", geolocationDto);

        return "/geolocation/edit";
    }


    @PostMapping("/edit")
    public String geolocationUpdate(@Valid @ModelAttribute GeolocationDto geolocationDto)
    {

        Geolocation geolocation = geolocationRepository.findById(geolocationDto.getId()).orElse(null);
        if(geolocation ==null)
        {
            return "/404";
        }

        geolocation.setAreaName(geolocationDto.getAreaName());

        geolocation.setLatitudeA(geolocationDto.getLatitudeA());
        geolocation.setLongitudeA(geolocationDto.getLongitudeA());

        geolocation.setLatitudeB(geolocationDto.getLatitudeB());
        geolocation.setLongitudeB(geolocationDto.getLongitudeB());

        geolocation.setLongitudeC(geolocationDto.getLongitudeC());
        geolocation.setLatitudeC(geolocationDto.getLatitudeC());

        geolocation.setLongitudeD(geolocationDto.getLongitudeD());
        geolocation.setLatitudeD(geolocationDto.getLatitudeD());

        geolocationRepository.save(geolocation);

        //Log the action
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        SystemLog systemLog = new SystemLog();
        systemLog.setCreatedAt(LocalDateTime.now());
        systemLog.setEmployee(currentUser);
        systemLog.setDescription("Update Geolocation name:"+geolocation.getAreaName());
        systemLogRepository.save(systemLog);


        return "redirect:/geolocation/geolocationDetail?id="+geolocationDto.getId();
    }
}
