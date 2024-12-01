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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
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
            model.addAttribute("keyword",keyword);

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

        //---------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +" | "+currentUser.getName();
        model.addAttribute("loginUser", loginUser);
        //--------

        return "device/list";
    } // GET List

    @GetMapping("/add")
    public String addDevicePage(Model model)
    {
        List<Employee> employees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        DeviceDto deviceDto = new DeviceDto();
        model.addAttribute("deviceDto",deviceDto);
        model.addAttribute("pageTitle","Add New Device");
        model.addAttribute("employees", employees);

        //---------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +" | "+currentUser.getName();
        model.addAttribute("loginUser", loginUser);
        //--------

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

        Employee deviceUser = employeeRepository.findByBadgeNumber(deviceDto.getBadgeNumber()).orElse(null);
        if(deviceUser !=null)
        {
            device.setUser(deviceUser);
        }

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

            List<Asset> deviceAssets = assetRepository.findByDevice(device).reversed();

            AddDeviceUserDto addDeviceUserDto = new AddDeviceUserDto();

            addDeviceUserDto.setSerialNumber(serialNumber);

            List<Employee> employees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

            model.addAttribute("employees", employees);
            model.addAttribute("deviceAssets",deviceAssets);
            model.addAttribute("pageTitle","Device Detail");
            model.addAttribute("addDeviceUserDto",addDeviceUserDto);
            model.addAttribute("device",device);

            //---------
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
            String loginUser = currentUser.getBadgeNumber() +"|"+currentUser.getName();
            model.addAttribute("loginUser", loginUser);
            //--------

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
        asset.setCreatedAt(LocalDateTime.now());

        assetRepository.save(asset);

        //update current user
        device.setUser(employee);
        deviceRepository.save(device);


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

    } //GET print Asset



    @PostMapping("/handover")
    public String deviceHandover(@RequestParam String assetCode, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date handoverDate, @RequestParam(required = false) String handoverNote)
    {
        Asset asset = assetRepository.findByCode(assetCode).orElse(null);
        if(asset !=null)
        {
            asset.setHandoverDate(handoverDate);
            asset.setNote(handoverNote);
            asset.setUpdatedAt(LocalDateTime.now());
            assetRepository.save(asset);
            return "redirect:/device/detail?serialNumber="+asset.getDevice().getSerialNumber();
        }

        return "/404";

    } //Post handover device


    @PostMapping("/importCSV")
    public String importDevices(ImportDeviceDto importDeviceDto, Model model)
    {
        String uploadDir = "public/upload/device/";
        Path uploadPath = Paths.get(uploadDir, "device");

        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {

            // Redirect to error page if the directory cannot be created
            model.addAttribute("message", e.getMessage());
            return "/404";
        }

        MultipartFile file = importDeviceDto.getCsvFile();

        // Validate file type
        if (!file.getOriginalFilename().endsWith(".csv")) {
            // Redirect to an error page for invalid file type
            model.addAttribute("message", "invalid file type");
            return "/404";
        }


        // Save the file
        Path filePath = uploadPath.resolve(file.getOriginalFilename());
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {

            // Redirect to an error page if the file cannot be saved
            model.addAttribute("message", e.getMessage());
            return "/404";
        }


        // Read and process the CSV file
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line by commas to get CSV columns
                String[] data = line.split(",");

                // Skip the header row or invalid rows
                if (data.length < 6 || "Serial Number".equalsIgnoreCase(data[0])) {
                    continue;
                }

                // Parse and insert data into the database
                try{
                    Device device = new Device();
                    device.setSerialNumber(data[0].trim()); //first csv column is serial number
                    device.setManufacture(data[1].trim()); // second column is Manufacture
                    device.setCategory(data[2].trim());    // third column is Category
                    device.setModel(data[3].trim());
                    device.setDescription(data[4].trim());

                    Date acquisitionDate = dateFormat.parse(data[5].trim()); //from string to date format [1965-05-20]
                    device.setAcquisitionDate(acquisitionDate);

                    device.setStatus(data[6].trim());
                    device.setCreatedAt(LocalDateTime.now());

                    deviceRepository.save(device);
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }

            }
        } catch (IOException e) {

            // Redirect to an error page if reading the file fails
            System.out.println(e.getMessage());
        } //------------------------------------------------

        return "redirect:/device/list";
    } // POST importCSV


    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response)
    {
        //create CSV file template for employee table
        //download to client pc
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=device_template.csv");
        List<Device> devices = deviceRepository.findAll();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.write('\ufeff');
            // Write CSV header row
            writer.println("Serial Number,Manufacture, Category, Model,Description,Acquisition Date, Status");

            devices.forEach(device -> {
                writer.println( device.getSerialNumber() +","+
                                device.getManufacture() +","+
                                 device.getCategory() +","+
                                device.getModel() +","+
                                device.getDescription() +","+
                                device.getAcquisitionDate() +","+
                                device.getStatus() );

            });


        } catch (IOException e) {
           System.out.println(e.getMessage());
        }

    } //GET CSV Template




}
