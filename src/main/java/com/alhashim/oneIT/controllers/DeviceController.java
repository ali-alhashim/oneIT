package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.dto.ImportDeviceDto;
import com.alhashim.oneIT.models.Device;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.DeviceRepository;
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
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    DeviceRepository deviceRepository;

    @GetMapping("/list")
    public String devicesList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size )
    {
        Page<Device> devicePage;

        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            devicePage = deviceRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all devices with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            devicePage = deviceRepository.findAll(pageable);
        }

        ImportDeviceDto importDeviceDto = new ImportDeviceDto();
        model.addAttribute("devices", devicePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", devicePage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("pageTitle","Devices List");
        model.addAttribute("totalItems", devicePage.getTotalElements());
        model.addAttribute("importDeviceDto",importDeviceDto);
        return "device/list";
    }
}
