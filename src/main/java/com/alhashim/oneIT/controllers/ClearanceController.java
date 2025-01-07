package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.EmployeeClearance;
import com.alhashim.oneIT.repositories.EmployeeClearanceRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
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

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;

@Controller
@RequestMapping("/clearance")
public class ClearanceController {

    @Autowired
    EmployeeClearanceRepository employeeClearanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

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

    @GetMapping("/clearanceDetail")
    public String clearanceDetail(@RequestParam Long id, Model model)
    {
        EmployeeClearance clearance = employeeClearanceRepository.findById(id).orElse(null);
        if(clearance ==null)
        {
            return "/404";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);

        model.addAttribute("clearance", clearance);
        model.addAttribute("currentUser",currentUser);
        return "/clearance/clearanceDetail";
    }

    @PostMapping("/uploadSignature")
    public String uploadSignature(@RequestParam Long clearanceId, @RequestParam("signature") String signature, @RequestParam String type)
    {
        EmployeeClearance clearance = employeeClearanceRepository.findById(clearanceId).orElse(null);
        if(clearance ==null)
        {
            return "/404";
        }
        try {
            String fileType="";
            // Decode base64 string
            byte[] decodedBytes = Base64.getDecoder().decode(signature.split(",")[1]);


            if(type.equalsIgnoreCase("Finance"))
            {
                fileType = "_financeSignature.png";
                clearance.setFinanceSignatureFileName(clearance.getId() + fileType);
            }

            // Define the directory and file path
            String directoryPath = "public/images/clearance/signature/";
            String filePath = directoryPath + clearance.getId() + fileType;

            // Create the directory if it doesn't exist
            Files.createDirectories(Paths.get(directoryPath));

            // Save the file
            try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                fos.write(decodedBytes);
            }




           employeeClearanceRepository.save(clearance);

            return "redirect:/clearance/clearanceDetail?id="+clearanceId;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to upload signature.";
        }
    }
}
