package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.dto.ImportAssetDto;
import com.alhashim.oneIT.models.Asset;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.AssetRepository;
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

@Controller
@RequestMapping("/asset")
public class AssetController {

    @Autowired
    AssetRepository assetRepository;

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

    }
}
