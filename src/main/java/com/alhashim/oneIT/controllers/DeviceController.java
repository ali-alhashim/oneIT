package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.dto.AddDeviceUserDto;
import com.alhashim.oneIT.dto.DeviceDto;
import com.alhashim.oneIT.dto.ImportDeviceDto;
import com.alhashim.oneIT.models.Asset;
import com.alhashim.oneIT.models.Device;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.AssetRepository;
import com.alhashim.oneIT.repositories.DeviceRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
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

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AssetRepository assetRepository;

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
    } // POST Add


    @GetMapping("/detail")
    public String deviceDetail(Model model, @RequestParam String serialNumber)
    {
        Device device = deviceRepository.findBySerialNumber(serialNumber).orElse(null);
        if(device !=null)
        {

            List<Employee> deviceEmployees = assetRepository.findEmployeesByDeviceSerialNumber(serialNumber);

            AddDeviceUserDto addDeviceUserDto = new AddDeviceUserDto();

            addDeviceUserDto.setSerialNumber(serialNumber);

            List<Employee> employees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

            model.addAttribute("employees", employees);
            model.addAttribute("deviceEmployees",deviceEmployees);
            model.addAttribute("pageTitle","Device Detail");
            model.addAttribute("addDeviceUserDto",addDeviceUserDto);
            model.addAttribute("device",device);
            return "device/detail";
        }
        else
        {
            model.addAttribute("message","No device with this Serial number ! "+serialNumber);
            return "/404";
        }

    } //GET Detail


    @PostMapping("/addDeviceUser")
    public String addDeviceUser(@Valid @ModelAttribute AddDeviceUserDto addDeviceUserDto, BindingResult result, Model model)
    {
        //-------- Create Next Asset Code
        String theNextAssetCode ="";
        String theLastAssetCode = assetRepository.findLastCode();

        if (theLastAssetCode != null && !theLastAssetCode.isEmpty()) {
            int numericPart = Integer.parseInt(theLastAssetCode.substring(2));
            numericPart++;
            theNextAssetCode = "IT" + String.format("%04d", numericPart);


        }
        else
        {
            theNextAssetCode = "IT0001";
        }
        //-----Asset Code

        if(result.hasErrors())
        {
            model.addAttribute("message",result.getFieldError());
            return "/404";
        }

        Device device = deviceRepository.findBySerialNumber(addDeviceUserDto.getSerialNumber()).orElse(null);
        if(device ==null)
        {
            model.addAttribute("message","Device Not Found with this serial number !"+addDeviceUserDto.getSerialNumber());
            return "/404";
        }

        Employee employee = employeeRepository.findByBadgeNumber(addDeviceUserDto.getBadgeNumber()).orElse(null);
        if(employee ==null)
        {
            model.addAttribute("message","Employee Not found with is Badge Number"+addDeviceUserDto.getBadgeNumber());
            return "/404";
        }

        Asset asset = new Asset();

        asset.setCode(theNextAssetCode);
        asset.setEmployee(employee);
        asset.setReceivedDate(addDeviceUserDto.getReceivedDate());
        asset.setDevice(device);

        assetRepository.save(asset);


        return "redirect:/device/printAsset?code="+theNextAssetCode;
    } // POST Add User to Device


    @GetMapping("/printAsset")
    public String printAsset(@RequestParam String code, Model model)
    {
        Asset asset = assetRepository.findByCode(code).orElse(null);
        if(asset !=null)
        {
            model.addAttribute("pageTitle","Device Asset");
            model.addAttribute("asset",asset);
            return "device/printAsset";
        }

        model.addAttribute("message", "No asset with this code: "+code);
        return "/404";

    }



}
