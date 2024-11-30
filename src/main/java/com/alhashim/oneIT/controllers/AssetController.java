package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.dto.ImportAssetDto;
import com.alhashim.oneIT.dto.ImportDeviceDto;
import com.alhashim.oneIT.models.Asset;

import com.alhashim.oneIT.models.Device;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.AssetRepository;
import com.alhashim.oneIT.repositories.DeviceRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/asset")
public class AssetController {

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @GetMapping("/list")
    public String assetList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<Asset> assetPage;

        ImportAssetDto importAssetDto = new ImportAssetDto();

        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            assetPage = assetRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all employees with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            assetPage = assetRepository.findAll(pageable);
        }


        //---------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +"|"+currentUser.getName();
        model.addAttribute("loginUser", loginUser);
        //--------


        model.addAttribute("assets", assetPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", assetPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", assetPage.getTotalElements());
        model.addAttribute("pageTitle","Assets List");
        model.addAttribute("importAssetDto", importAssetDto);
        return "asset/list";
    } //GET LIST


    @PostMapping("/uploadSignature")
    public String uploadSignature(@RequestParam("signature") String signature, @RequestParam String assetCode) {
        Asset asset = assetRepository.findByCode(assetCode).orElse(null);
        if(asset !=null)
        {
            try {
                // Decode base64 string
                byte[] decodedBytes = Base64.getDecoder().decode(signature.split(",")[1]);

                // Define the directory and file path
                String directoryPath = "public/images/asset/signature/";
                String filePath = directoryPath + assetCode + "_signature.png";

                // Create the directory if it doesn't exist
                Files.createDirectories(Paths.get(directoryPath));

                // Save the file
                try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                    fos.write(decodedBytes);
                }


                asset.setSignatureFileName(assetCode + "_signature.png");
                asset.setConfirmReceived(true);
                asset.setConfirmationDate(LocalDateTime.now());
                assetRepository.save(asset);

                return "redirect:/asset/list";
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to upload signature.";
            }
        }

        return "/404";

    } //POST Upload signature


    @PostMapping("/importCSV")
    public String importDevices(ImportAssetDto importAssetDto, Model model)
    {
        String uploadDir = "public/upload/asset/";
        Path uploadPath = Paths.get(uploadDir, "asset");

        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {

            // Redirect to error page if the directory cannot be created
            model.addAttribute("message", e.getMessage());
            return "/404";
        }

        MultipartFile file = importAssetDto.getCsvFile();

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            int rowNumber = 0;

            while ((line = br.readLine()) != null) {
                // Split the line by commas to get CSV columns
                rowNumber++;

                String[] data = line.split(",");

                // Skip the header row or invalid rows
                if(data.length < 4)
                {
                    System.out.println("Badge number not exist in CSV row" + rowNumber);
                    continue;
                }

                if (data[3].trim().isEmpty() || data[3].equals("Badge Number")) {
                    System.out.println("Badge number not exist in CSV row" + rowNumber);
                    continue;
                }

                // Parse and insert data into the database
                try{

                    Asset asset = new Asset();


                    LocalDateTime createdAt = LocalDateTime.parse(data[0].trim(), formatter);
                    asset.setCreatedAt(createdAt); //first csv column Asset Created At


                    asset.setCode(data[1].trim()); // Asset Code

                    Device device = deviceRepository.findBySerialNumber(data[2].trim()).orElse(null);
                    if(device ==null)
                    {
                        //skip row without serial number or the device not available in database
                        System.out.println("skip row without serial number or the device not available in database row#:" + rowNumber);
                        continue;
                    }
                    asset.setDevice(device);

                    Employee employee = employeeRepository.findByBadgeNumber(data[3].trim()).orElse(null);
                    if(employee ==null)
                    {
                        // skip row without badge number or the employee not exist in database
                        System.out.println("skip row without badge number or the employee not exist in database row#:" + rowNumber);
                        continue;
                    }
                    asset.setEmployee(employee);

                    Date receivedDate = dateFormat.parse(data[4].trim()); //from string to date format [1965-05-20]
                    asset.setReceivedDate(receivedDate);

                    if(data.length > 5)
                    {
                        Date handoverDate = dateFormat.parse(data[5].trim()); //from string to date format [1965-05-20]
                        asset.setHandoverDate(handoverDate);
                    }
                    else
                    {
                        //no hand over so the device with the user
                        device.setUser(employee);
                        deviceRepository.save(device);
                    }




                    assetRepository.save(asset);
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

        return "redirect:/asset/list";
    } // POST importCSV


    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response)
    {
        //create CSV file template for employee table
        //download to client pc
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=asset_template.csv");

        List<Asset> assets = assetRepository.findAll();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.write('\ufeff');
            // Write CSV header row
            writer.println("CreatedAt, Asset Code, Serial Number, Badge Number, Received Date, Handover Date");

            assets.forEach(asset -> {
                writer.println(
                        asset.getCreatedAt() +","+
                        asset.getCode() +","+
                        asset.getDevice().getSerialNumber()+","+
                        asset.getEmployee().getBadgeNumber()+","+
                        asset.getReceivedDate()+","+
                        asset.getHandoverDate()


                );

            });


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    } //GET CSV Template


}
