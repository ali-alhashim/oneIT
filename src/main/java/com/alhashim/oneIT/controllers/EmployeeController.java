package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.dto.EmployeeDto;
import com.alhashim.oneIT.models.Department;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Role;
import com.alhashim.oneIT.repositories.DepartmentRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.RoleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DepartmentRepository departmentRepository;


    @GetMapping("/list")
    public String employeeList(Model model)
    {

        List<Employee> employees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("employees", employees);
        model.addAttribute("pageTitle","Employees List");
        return "employee/list";
    }

    @GetMapping("/add")
    public String addEmployeePage(Model model)
    {
        EmployeeDto employeeDto = new EmployeeDto();
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("employeeDto",employeeDto);
        model.addAttribute("pageTitle","Add New Employee");
        model.addAttribute("departments",departments);
        return "employee/add";
    }

    @PostMapping("/add")
    public String CreateEmployee(@Valid @ModelAttribute EmployeeDto employeeDto, BindingResult result, Model model)
    {
        String theNextBadgeNumber ="";
        String storageFileName ="";
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments",departments);



        if(employeeDto.getBadgeNumber().isEmpty())
        {
            System.out.println("Badge Number is Empty generate auto badgeNumber");
                //generate auto badgeNumber
                //get last employee badge number like A0000 + 1 the new = A0001
               String theLastBadgeNumber = employeeRepository.findLastBadgeNumber();

               System.out.println("the last badge number was : "+theLastBadgeNumber);

                if (theLastBadgeNumber != null && !theLastBadgeNumber.isEmpty()) {
                    int numericPart = Integer.parseInt(theLastBadgeNumber.substring(1));
                    numericPart++;
                    theNextBadgeNumber = "A" + String.format("%04d", numericPart);


                }
                else
                {
                    theNextBadgeNumber = "A00001";
                }



        }
        else
        {
            theNextBadgeNumber = employeeDto.getBadgeNumber();
        }


        if(!employeeDto.getImageFile().isEmpty())
        {
            //Save image file
            MultipartFile image = employeeDto.getImageFile();
            Date createdAt = new Date();
            storageFileName = createdAt.getTime()+"_"+ image.getOriginalFilename();
            String uploadDir = "public/images/"+theNextBadgeNumber+"/";
            Path uploadPath = Paths.get(uploadDir);


           try
           {
               if(!Files.exists(uploadPath))
               {
                   Files.createDirectories(uploadPath);
               }

               InputStream inputStream = image.getInputStream();
               Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);

           }
           catch (Exception e)
           {
               System.out.println("saving image file exception "+ e.getMessage() );

               result.addError(new FieldError("employeeDto", "imageFile", e.getMessage()));
               return "employee/add";

           }


        }

           Employee employee = new Employee();

           employee.setBadgeNumber(theNextBadgeNumber);

           employee.setName(employeeDto.getName());
           employee.setCreatedAt(LocalDateTime.now());
           employee.setPersonalMobile(employeeDto.getPersonalMobile());
           employee.setArName(employeeDto.getArName());
           employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));

           employee.setBirthDate(employeeDto.getBirthDate());

           employee.setGender(employeeDto.getGender());
           employee.setImageFileName(storageFileName);
           employee.setOfficeLocation(employeeDto.getOfficeLocation());
           employee.setStatus(employeeDto.getStatus());
           employee.setWorkMobile(employeeDto.getWorkMobile());
           employee.setWorkEmail(employeeDto.getWorkEmail());

           Department department = departmentRepository.findById(employeeDto.getDepartment()).orElse(null);
           if(department !=null)
           {
               employee.setDepartment(department);
           }

            Set<Role> roles = new HashSet<>();

           if(employeeDto.isIs_MANAGER())
           {
               Role role = roleRepository.findByRoleName("MANAGER").orElse(null);
               roles.add(role);
               employee.setRoles(roles);
           }


            if(employeeDto.isIs_USER())
            {
                Role role = roleRepository.findByRoleName("USER").orElse(null);

                roles.add(role);

                employee.setRoles(roles);
            }

            if(employeeDto.isIs_SUPERADMIN())
            {
                Role role = roleRepository.findByRoleName("SUPERADMIN").orElse(null);
                roles.add(role);
                employee.setRoles(roles);
            }

            if(employeeDto.isIs_SUPPORT())
            {
                Role role = roleRepository.findByRoleName("SUPPORT").orElse(null);
                roles.add(role);
                employee.setRoles(roles);
            }


           try
           {
               employeeRepository.save(employee);
           }
           catch (Exception e)
           {
               System.out.println(e.getMessage());

               return "employee/add";
           }



            return "redirect:/employee/list";

    }

    @GetMapping("/detail")
    public  String detailPage(Model model, @RequestParam String badgeNumber)
    {
        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber).orElse(null);

        return "employee/detail";
    }


}
