package com.alhashim.oneIT.controllers;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.alhashim.oneIT.dto.DocumentDto;
import com.alhashim.oneIT.models.Document;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.DocumentRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;

@Controller
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @PostMapping("/upload")
    public String uploadEmployeeDocument(@Valid @ModelAttribute DocumentDto documentDto, BindingResult result, Model model)
    {
        if(result.hasErrors())
        {
            model.addAttribute("message", result.getAllErrors());
            return "/404";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);

        if(currentUser ==null)
        {
            return "/404";
        }

        Document document = new Document();

        if(!documentDto.getDocumentFile().isEmpty())
        {
            MultipartFile documentFile = documentDto.getDocumentFile();
            if (documentFile.getSize() > 5 * 1024 * 1024) { // 5MB in bytes
                model.addAttribute("message", "You exceed the allowed file size. Maximum: 5MB");
                return "/404";
            }

            if (!documentFile.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                model.addAttribute("message", "Only .PDF format is allowed.");
                return "/404";
            }

            String  storageFileName = documentDto.getType()+"_"+ documentFile.getOriginalFilename();
            String uploadDir = "public/document/"+currentUser.getBadgeNumber()+"/";
            Path uploadPath = Paths.get(uploadDir);

            try
            {
                if(!Files.exists(uploadPath))
                {
                    Files.createDirectories(uploadPath);
                }

                InputStream inputStream = documentFile.getInputStream();
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);


                document.setFileName(storageFileName);
                document.setEmployee(currentUser);
                document.setFileSize(documentFile.getSize());
                document.setDescription(documentDto.getDescription());
                document.setFileType(documentDto.getType());
                documentRepository.save(document);

            }
            catch (Exception e)
            {
                model.addAttribute("message", e.getMessage());
                return "/404";
            }
        }
        else{
            model.addAttribute("message","Document is empty !");
            return "/404";
        }

        return "redirect:/dashboard";
    }

    //------
    @GetMapping("/{badgeNumber}/{fileName}")
    public ResponseEntity<Resource> getDocument( @PathVariable String badgeNumber,  @PathVariable String fileName, Principal principal)
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        Document document = documentRepository.findByFileName(fileName).orElse(null);

        if(document ==null)
        {
            return ResponseEntity.notFound().build();
        }

        if(currentUser == null)
        {
            return ResponseEntity.notFound().build();
        }


        // Check if the user is Not owner or Not admin or  Not HR then FORBIDDEN
        if (currentUser != document.getEmployee() && !currentUser.isAdmin() && !currentUser.isHR())
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Load the document
        Resource file = loadDocument(badgeNumber, fileName);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        // Return the file as a response
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file);
    }





    private Resource loadDocument(String badgeNumber, String fileName) {
        // Load the document from storage
        Path filePath = Paths.get("public/document/" + badgeNumber + "/" + fileName);
        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            return null;
        }
    }
    //------
}
