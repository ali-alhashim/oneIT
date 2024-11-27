package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.dto.DeviceDto;
import com.alhashim.oneIT.dto.ImportDeviceDto;
import com.alhashim.oneIT.models.Device;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.DeviceRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Date;
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
    } // GET List

    @GetMapping("/add")
    public String addDevicePage(Model model)
    {
        DeviceDto deviceDto = new DeviceDto();
        model.addAttribute("deviceDto",deviceDto);
        model.addAttribute("pageTitle","Add New Device");
        return "device/add";
    } //GET add

    @PostMapping("/add")
    public String addDevice(@Valid @ModelAttribute DeviceDto deviceDto, BindingResult result, Model model)
    {
        if(result.hasErrors())
        {
            return "device/add";
        }

        Device device = new Device();
        //check if device not exist
        Device checkDevice = deviceRepository.findBySerialNumber(device.getSerialNumber()).orElse(null);
        if(checkDevice !=null)
        {
            result.addError(new FieldError("deviceDto", "serialNumber", "The Device Already Exist !"));
            return "device/add";
        }

        //Save the new device
        device.setSerialNumber(deviceDto.getSerialNumber());
        device.setCategory(deviceDto.getCategory());
        device.setDescription(deviceDto.getDescription());
        device.setStatus(deviceDto.getStatus());
        device.setManufacture(deviceDto.getManufacture());
        device.setCreatedAt(LocalDateTime.now());
        device.setAcquisitionDate(deviceDto.getAcquisitionDate());
        device.setPurchasePrice(deviceDto.getPurchasePrice());

        //upload photo if exist
        if(!deviceDto.getImageFile().isEmpty())
        {
            MultipartFile image = deviceDto.getImageFile();
            Date createdAt = new Date();
            String storageFileName = createdAt.getTime()+"_"+ image.getOriginalFilename();
            String uploadDir = "public/images/device/"+deviceDto.getSerialNumber()+"/";
            Path uploadPath = Paths.get(uploadDir);

            try
            {
                if(!Files.exists(uploadPath))
                {
                    Files.createDirectories(uploadPath);
                }
                InputStream inputStream = image.getInputStream();
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);

                device.setImageFileName(storageFileName);
            }
            catch (Exception e)
            {
                model.addAttribute("message",e.getMessage());
                return "/404";
            }

        } //upload image


        deviceRepository.save(device);
        return "redirect:/device/list";
    }
}
