package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.dto.*;
import com.alhashim.oneIT.models.*;
import com.alhashim.oneIT.repositories.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;

import java.time.ZoneId;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    SystemLogRepository systemLogRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    SalaryRepository salaryRepository;

    @Autowired
    EmployeeClearanceRepository employeeClearanceRepository;

    @Autowired
    NotificationRepository notificationRepository;

       //Employee list only with role of ROLE_ADMIN ROLE_HR, ROLE_SUPPORT
    @GetMapping("/list")
    public String employeeList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size )
    {
        //for search
        Page<Employee> employeePage;
        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            employeePage = employeeRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all employees with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            employeePage = employeeRepository.findAll(pageable);
        }
        // ---

        ImportEmployeeDto importEmployeeDto = new ImportEmployeeDto();
        model.addAttribute("importEmployeeDto", importEmployeeDto);
        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", employeePage.getTotalElements());
        model.addAttribute("pageTitle","Employees List");

        //---------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +" | "+currentUser.getName();
        model.addAttribute("loginUser", loginUser);
        //--------

        return "employee/list";
    } // GET List


    //role of ROLE_ADMIN, HR, SUPPORT
    @GetMapping("/add")
    public String addEmployeePage(Model model)
    {
        EmployeeDto employeeDto = new EmployeeDto();
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("employeeDto",employeeDto);
        model.addAttribute("pageTitle","Add New Employee");
        model.addAttribute("departments",departments);


        //---------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +" | "+currentUser.getName();
        model.addAttribute("loginUser", loginUser);
        //--------

        return "employee/add";
    } // GET Add

    //role of admin, HR, SUPPORT only can access from securityConfig
    @PostMapping("/add")
    public String CreateEmployee(@Valid @ModelAttribute EmployeeDto employeeDto, BindingResult result, Model model)
    {
        String theNextBadgeNumber ="";
        String storageFileName ="";
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments",departments);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);



        if(employeeDto.getBadgeNumber().isEmpty())
        {
            System.out.println("Badge Number is Empty generate auto badgeNumber");
                //generate auto badgeNumber
                //get last employee badge number like A0000 + 1 the new = A0001
               String theLastBadgeNumber = employeeRepository.findLastBadgeNumberForCompany();

               System.out.println("the last badge number was : "+theLastBadgeNumber);

                if (theLastBadgeNumber != null && !theLastBadgeNumber.isEmpty()) {
                    int numericPart = Integer.parseInt(theLastBadgeNumber.substring(1));
                    numericPart++;
                    theNextBadgeNumber = "A" + String.format("%04d", numericPart);


                }
                else
                {
                    theNextBadgeNumber = "A0001";
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

           //-
           employee.setArJobTitle(employeeDto.getArJobTitle());
           employee.setJobTitle(employeeDto.getJobTitle());
           employee.setBusinessUnit(employeeDto.getBusinessUnit());
           employee.setHireDate(employeeDto.getHireDate());
           employee.setCitizenship(employeeDto.getCitizenship());
           employee.setTerminationDate(employeeDto.getTerminationDate());
           employee.setGovId(employeeDto.getGovId());
           employee.setMaritalStatus(employeeDto.getMaritalStatus());
           employee.setSponsorName(employeeDto.getSponsorName());
           //-

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

        if(employeeDto.getIs_MEDICAL())
        {
            Role role = roleRepository.findByRoleName("MEDICAL").orElse(null);
            roles.add(role);
            employee.setRoles(roles);
        }

        if(employeeDto.getIs_VEHICLE())
        {
            Role role = roleRepository.findByRoleName("VEHICLE").orElse(null);
            roles.add(role);
            employee.setRoles(roles);
        }

        if(employeeDto.getIs_FINANCE())
        {
            Role role = roleRepository.findByRoleName("FINANCE").orElse(null);
            roles.add(role);
            employee.setRoles(roles);
        }

        if(employeeDto.getIs_PROCUREMENT())
        {
            Role role = roleRepository.findByRoleName("PROCUREMENT").orElse(null);
            roles.add(role);
            employee.setRoles(roles);
        }


            if(employeeDto.isIs_USER())
            {
                Role role = roleRepository.findByRoleName("USER").orElse(null);

                roles.add(role);

                employee.setRoles(roles);
            }


        if(employeeDto.isIs_HR())
        {
            Role role = roleRepository.findByRoleName("HR").orElse(null);

            roles.add(role);

            employee.setRoles(roles);
        }


            //this role can be given only with admin
          if(currentUser.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getRoleName())))
          {
              if(employeeDto.isIs_ADMIN())
              {
                  Role role = roleRepository.findByRoleName("ADMIN").orElse(null);
                  roles.add(role);
                  employee.setRoles(roles);
              }
          }
          else
          {
              System.out.println("if you trying add admin role to new user ! you are Not admin");
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

               // Log the  action

               SystemLog systemLog = new SystemLog();
               systemLog.setCreatedAt(LocalDateTime.now());
               systemLog.setEmployee(currentUser);
               systemLog.setDescription("Add New Employee Badge#"+employee.getBadgeNumber() +" Name:"+employee.getName());
               systemLogRepository.save(systemLog);

           }
           catch (Exception e)
           {
               System.out.println(e.getMessage());

               return "employee/add";
           }



            return "redirect:/employee/list";

    } // POST Add

    //role of admin, HR, SUPPORT
    @GetMapping("/detail")
    public  String detailPage(Model model, @RequestParam String badgeNumber, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber).orElse(null);
        if(employee !=null)
        {
            Page<SystemLog> systemLogPage;
            systemLogPage = systemLogRepository.findByEmployeeId(employee.getId(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));

            RestPasswordDto restPasswordDto = new RestPasswordDto();
            TerminationDto terminationDto = new TerminationDto();
            SalaryDto salaryDto = new SalaryDto();

            List<Salary> salaries = salaryRepository.findByEmployee(employee);
            if(salaries.isEmpty())
            {
                System.out.println("there are no salaries for this employee !!!");
            }
            System.out.println("This Employee has : "+ salaries.size() +" Salaries..............");

            String MFA = "...";
            if(employee.getOtpCode() == null)
            {
                 MFA = "not yet";
            }
            else
            {
                MFA = "Active";
            }

            restPasswordDto.setBadgeNumber(badgeNumber);
            model.addAttribute("salaries", salaries.reversed());
            model.addAttribute("salaryDto", salaryDto);
            model.addAttribute("restPasswordDto",restPasswordDto);
            model.addAttribute("terminationDto", terminationDto);
            model.addAttribute("pageTitle","Employee Detail");
            model.addAttribute("badgeNumber", badgeNumber);
            model.addAttribute("userName", employee.getName());
            model.addAttribute("userArName", employee.getArName());
            model.addAttribute("govId", employee.getGovId());
            model.addAttribute("MFA", MFA);
            model.addAttribute("employeeId", employee.getId());
            model.addAttribute("imageFileName", employee.getImageFileName());
            model.addAttribute("workEmail", employee.getWorkEmail());
            model.addAttribute("workMobile",employee.getWorkMobile());
            model.addAttribute("personalEmail", employee.getPersonalEmail());
            model.addAttribute("personalMobile", employee.getPersonalMobile());
            model.addAttribute("officeLocation", employee.getOfficeLocation());
            model.addAttribute("businessUnit", employee.getBusinessUnit());
            model.addAttribute("maritalStatus", employee.getMaritalStatus());
            model.addAttribute("status", employee.getStatus());
            model.addAttribute("sponsorName", employee.getSponsorName());
            model.addAttribute("arJobTitle", employee.getArJobTitle());
            model.addAttribute("jobTitle", employee.getJobTitle());
            model.addAttribute("citizenship", employee.getCitizenship());
            model.addAttribute("roles", employee.getRoles());
            model.addAttribute("birthDate", employee.getBirthDate());
            model.addAttribute("hireDate", employee.getHireDate());
            model.addAttribute("terminationDate", employee.getTerminationDate());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", systemLogPage.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("totalItems", systemLogPage.getTotalElements());


            int laptopCount = deviceRepository.countByEmployeeAndCategory(employee, "Laptop");
            model.addAttribute("laptopCount",laptopCount);

            int smartPhoneCount = deviceRepository.countByEmployeeAndCategory(employee, "SmartPhone");
            model.addAttribute("smartPhoneCount",smartPhoneCount);

            int monitorCount = deviceRepository.countByEmployeeAndCategory(employee, "Monitor");
            model.addAttribute("monitorCount",monitorCount);

            int printerCount = deviceRepository.countByEmployeeAndCategory(employee, "Printer");
            model.addAttribute("printerCount",printerCount);




            //
            model.addAttribute("systemLogs",systemLogPage.getContent() );

            //---------
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
            String loginUser = currentUser.getBadgeNumber() +"|"+currentUser.getName();
            model.addAttribute("loginUser", loginUser);
            //--------

            model.addAttribute("departmentName", employee.getDepartment() !=null ? employee.getDepartment().getName():"No Department");
        }
        else
        {
            model.addAttribute("message","No Employee with this Badge Number "+badgeNumber);
            return "/404";
        }

        return "employee/detail";
    } // GET Detail


    //role of SUPERADMIN, HR
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response)
    {
        //create CSV file template for employee table
        //download to client pc
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=employee_template.csv");
        List<Employee> employees = employeeRepository.findAll();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.write('\ufeff');
            // Write CSV header row
            writer.println("badgeNumber,name,arName,workEmail,workMobile,status,birthDate,gender");

            employees.forEach(employee -> {
                writer.println( employee.getBadgeNumber() +","+ employee.getName() +","+ employee.getArName() +","+ employee.getWorkEmail() +","+ employee.getWorkMobile() +","+ employee.getStatus() +","+ employee.getBirthDate() +","+ employee.getGender());
            });


        } catch (IOException e) {
            e.printStackTrace(); // Log the error in production
        }

    } //GET CSV Template

    //role of SUPERADMIN
    @PostMapping("/importCSV")
    public String importCSV(ImportEmployeeDto importEmployeeDto, Model model)
    {
        // upload CSV file to public/upload/employee/
        // for security check only upload .csv file
        // read CSV each row and insert to our DB
        // Base directory for file uploads
        String uploadDir = "public/upload/employee/";

        Path uploadPath = Paths.get(uploadDir, "employee");
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            e.printStackTrace();
            // Redirect to error page if the directory cannot be created
            model.addAttribute("message", e.getMessage());
            return "/404";
        }

        MultipartFile file = importEmployeeDto.getCsvFile();

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
            e.printStackTrace();
            // Redirect to an error page if the file cannot be saved
            model.addAttribute("message", e.getMessage());
            return "/404";
        }

        // Read and process the CSV file
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //get USER role
        Role userRole = roleRepository.findByRoleName("USER").orElse(null);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line by commas to get CSV columns
                String[] data = line.split(",");

                // Skip the header row or invalid rows
                if (data.length < 1 || "badgeNumber".equalsIgnoreCase(data[0])) {
                    continue;
                }

                try {
                    Employee employee = new Employee();
                    employee.setBadgeNumber(data[0].trim()); // Mandatory field

                    // Use conditional checks for optional fields
                    employee.setName(data.length > 1 ? data[1].trim() : null);
                    employee.setArName(data.length > 2 ? data[2].trim() : null);
                    employee.setWorkEmail(data.length > 3 ? data[3].trim() : null);
                    employee.setWorkMobile(data.length > 4 ? data[4].trim() : null);
                    employee.setStatus(data.length > 5 ? data[5].trim() : null);

                    // Handle birthDate parsing safely
                    if (data.length > 6 && !data[6].trim().isEmpty()) {
                        Date birthDate = dateFormat.parse(data[6].trim());
                        employee.setBirthDate(birthDate);
                    } else {
                        employee.setBirthDate(null);
                    }

                    // Handle gender
                    employee.setGender(data.length > 7 ? data[7].trim() : null);

                    // Set other fields
                    employee.setRoles(roles);
                    employee.setCreatedAt(LocalDateTime.now());

                    // Save to the repository
                    employeeRepository.save(employee);
                } catch (Exception e) {
                    System.out.println("Error processing line: " + line);
                    System.out.println("Exception: " + e.getMessage());
                }



            }
        } catch (IOException e) {
            e.printStackTrace();
            // Redirect to an error page if reading the file fails
           System.out.println(e.getMessage());
        } //------------------------------------------------



        return "redirect:/employee/list";
    } // POST import CSV



    //role of SUPERADMIN, HR, SUPPORT
    //but the roles only admin
    @GetMapping("/edit")
    public  String editPage(Model model, @RequestParam String badgeNumber)
    {
        System.out.println("GET Edit Page Incoming badgeNumber: " + badgeNumber);

        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber).orElse(null);
        if(employee !=null)
        {
            EmployeeDto employeeDto = new EmployeeDto();

            employeeDto.setBadgeNumber(employee.getBadgeNumber());

            employeeDto.setName(employee.getName());
            employeeDto.setArName(employee.getArName());

            if(employee.getDepartment() !=null)
            {
                employeeDto.setDepartment(employee.getDepartment().getId());
            }


            employeeDto.setGender(employee.getGender());
            employeeDto.setBirthDate(employee.getBirthDate());
            employeeDto.setOfficeLocation(employee.getOfficeLocation());
            employeeDto.setWorkEmail(employee.getWorkEmail());
            employeeDto.setWorkMobile(employee.getWorkMobile());
            employeeDto.setPersonalEmail(employee.getPersonalEmail());
            employeeDto.setPersonalMobile(employee.getPersonalMobile());
            employeeDto.setStatus(employee.getStatus());

            //-
            employeeDto.setArJobTitle(employee.getArJobTitle());
            employeeDto.setJobTitle(employee.getJobTitle());
            employeeDto.setBusinessUnit(employee.getBusinessUnit());
            employeeDto.setHireDate(employee.getHireDate());
            employeeDto.setCitizenship(employee.getCitizenship());
            employeeDto.setTerminationDate(employee.getTerminationDate());
            employeeDto.setGovId(employee.getGovId());
            employeeDto.setMaritalStatus(employee.getMaritalStatus());
            employeeDto.setSponsorName(employee.getSponsorName());
            //-

            if(employee.getRoles().stream().anyMatch(role -> "USER".equals(role.getRoleName())))
            {
                employeeDto.setIs_USER(true);
            }

            if(employee.getRoles().stream().anyMatch(role -> "MANAGER".equals(role.getRoleName())))
            {
                employeeDto.setIs_MANAGER(true);
            }

            if(employee.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getRoleName())))
            {
                employeeDto.setIs_ADMIN(true);
            }

            if(employee.getRoles().stream().anyMatch(role -> "SUPPORT".equals(role.getRoleName())))
            {
                employeeDto.setIs_SUPPORT(true);
            }

            if(employee.getRoles().stream().anyMatch(role -> "HR".equals(role.getRoleName())))
            {
                employeeDto.setIs_HR(true);
            }

            if(employee.getRoles().stream().anyMatch(role -> "PROCUREMENT".equals(role.getRoleName())))
            {
                employeeDto.setIs_PROCUREMENT(true);
            }

            if(employee.getRoles().stream().anyMatch(role -> "MEDICAL".equals(role.getRoleName())))
            {
                employeeDto.setIs_MEDICAL(true);
            }

            if(employee.getRoles().stream().anyMatch(role -> "VEHICLE".equals(role.getRoleName())))
            {
                employeeDto.setIs_VEHICLE(true);
            }

            if(employee.getRoles().stream().anyMatch(role -> "FINANCE".equals(role.getRoleName())))
            {
                employeeDto.setIs_FINANCE(true);
            }


            List<Department> departments = departmentRepository.findAll();
            model.addAttribute("employeeDto",employeeDto);
            model.addAttribute("pageTitle","Edit Employee");
            model.addAttribute("departments",departments);

            //---------
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
            String loginUser = currentUser.getBadgeNumber() +"|"+currentUser.getName();
            model.addAttribute("loginUser", loginUser);
            //--------

            return "employee/edit";
        }
        else
        {
            model.addAttribute("message", "GET Edit Page:No Employee with this Badge Number: "+badgeNumber);
            return "/404";
        }

    } //--------GET edit

    //role of SUPERADMIN, HR, SUPPORT
    @PostMapping("/edit")
    public String updateEmployee(@Valid @ModelAttribute EmployeeDto employeeDto, BindingResult result, Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);


        System.out.println("employeeDto = " + employeeDto.toString());



        Employee employee  = employeeRepository.findByBadgeNumber(employeeDto.getBadgeNumber()).orElse(null);

        if(employee !=null)
        {
            System.out.println("updating "+employee.getName() +" information ...");

            employee.setOfficeLocation(employeeDto.getOfficeLocation());
            employee.setWorkMobile(employeeDto.getWorkMobile());
            employee.setPersonalMobile(employeeDto.getPersonalMobile());
            employee.setWorkEmail(employeeDto.getWorkEmail());
            employee.setName(employeeDto.getName());
            employee.setArName(employee.getArName());

            //-
            employee.setArJobTitle(employeeDto.getArJobTitle());
            employee.setJobTitle(employeeDto.getJobTitle());
            employee.setBusinessUnit(employeeDto.getBusinessUnit());
            employee.setHireDate(employeeDto.getHireDate());
            employee.setCitizenship(employeeDto.getCitizenship());
            employee.setTerminationDate(employeeDto.getTerminationDate());
            employee.setGovId(employeeDto.getGovId());
            employee.setMaritalStatus(employeeDto.getMaritalStatus());
            employee.setSponsorName(employeeDto.getSponsorName());
            //-

            employee.setPersonalEmail(employeeDto.getPersonalEmail());

            Department department = departmentRepository.findById(employeeDto.getDepartment()).orElse(null);
            if(department !=null)
            {
               employee.setDepartment(department);
            }
            employee.setBirthDate(employeeDto.getBirthDate());
            employee.setGender(employeeDto.getGender());
            employee.setStatus(employeeDto.getStatus());




            //-------------Roles only admin can set admin role roles


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

            if( currentUser.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("ADMIN"))) {

                if (employeeDto.isIs_ADMIN()) {
                    Role role = roleRepository.findByRoleName("ADMIN").orElse(null);
                    roles.add(role);
                    employee.setRoles(roles);
                }

            } // only admin can give this role



                if(employeeDto.isIs_SUPPORT())
                {
                    Role role = roleRepository.findByRoleName("SUPPORT").orElse(null);
                    roles.add(role);
                    employee.setRoles(roles);
                }

                if(employeeDto.isIs_HR())
                {
                    Role role = roleRepository.findByRoleName("HR").orElse(null);
                    roles.add(role);
                    employee.setRoles(roles);
                }

            if(employeeDto.getIs_PROCUREMENT())
            {
                Role role = roleRepository.findByRoleName("PROCUREMENT").orElse(null);
                roles.add(role);
                employee.setRoles(roles);
            }

            if(employeeDto.getIs_MEDICAL())
            {
                Role role = roleRepository.findByRoleName("MEDICAL").orElse(null);
                roles.add(role);
                employee.setRoles(roles);
            }
            if(employeeDto.getIs_VEHICLE())
            {
                Role role = roleRepository.findByRoleName("VEHICLE").orElse(null);
                roles.add(role);
                employee.setRoles(roles);
            }

            if(employeeDto.getIs_FINANCE())
            {
                Role role = roleRepository.findByRoleName("FINANCE").orElse(null);
                roles.add(role);
                employee.setRoles(roles);
            }
                //-------------/Roles




            //-------upload photo if exist -----------

            if(!employeeDto.getImageFile().isEmpty())
            {
                String storageFileName ="";
                //Save image file
                MultipartFile image = employeeDto.getImageFile();
                Date createdAt = new Date();
                storageFileName = createdAt.getTime()+"_"+ image.getOriginalFilename();
                String uploadDir = "public/images/"+employeeDto.getBadgeNumber()+"/";
                Path uploadPath = Paths.get(uploadDir);


                try
                {
                    if(!Files.exists(uploadPath))
                    {
                        Files.createDirectories(uploadPath);
                    }

                    InputStream inputStream = image.getInputStream();
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);

                    employee.setImageFileName(storageFileName);

                }
                catch (Exception e)
                {
                    System.out.println("saving image file exception "+ e.getMessage() );

                    result.addError(new FieldError("employeeDto", "imageFile", e.getMessage()));
                    return "employee/edit?badgeNumber="+employeeDto.getBadgeNumber();

                }


            }
            //-------/upload photo--------------------
            employeeRepository.save(employee);

            // Log the  action

            SystemLog systemLog = new SystemLog();
            systemLog.setCreatedAt(LocalDateTime.now());
            systemLog.setEmployee(currentUser);
            systemLog.setDescription("Edit Employee Badge#"+employee.getBadgeNumber() +" Name:"+employee.getName());
            systemLogRepository.save(systemLog);

            return "redirect:/employee/detail?badgeNumber="+employee.getBadgeNumber();
        }
        else
        {
            System.out.println("No Employee with is Badge Number ! "+employeeDto.getBadgeNumber());
            if(result.hasErrors())
            {
                model.addAttribute("message", result.getFieldError());
                return "/404";
            }

            model.addAttribute("message", "POST EDIT Page:No Employee with this Badge Number: "+employeeDto.getBadgeNumber());
            return "/404";
        }




    } //POST update employee


    //POST Reset password

    //role of SUPERADMIN, HR, SUPPORT
    @PostMapping("/restPassword")
    public String resetPassword(@Valid @ModelAttribute RestPasswordDto restPasswordDto, Model model, RedirectAttributes redirectAttributes)
    {
        System.out.println("you want to reset password for employee with badgeNumber "+restPasswordDto.getBadgeNumber());
        Employee employee = employeeRepository.findByBadgeNumber(restPasswordDto.getBadgeNumber()).orElse(null);

        if(employee !=null)
        {
            employee.setPassword(passwordEncoder.encode(restPasswordDto.getPassword()));
            employeeRepository.save(employee);

            // Log the  action
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
            SystemLog systemLog = new SystemLog();
            systemLog.setCreatedAt(LocalDateTime.now());
            systemLog.setEmployee(currentUser);
            systemLog.setDescription("Reset Password Employee Badge#"+employee.getBadgeNumber() +" Name:"+employee.getName());
            systemLogRepository.save(systemLog);

            redirectAttributes.addFlashAttribute("sweetMessage", "Password has been Reset Successfully");
            return "redirect:/employee/detail?badgeNumber="+employee.getBadgeNumber();
        }
        model.addAttribute("message", "No Employee with this Badge Number ! "+restPasswordDto.getBadgeNumber());
        return "/404";
    } //reset password

    @PostMapping("/resetOTP")
    public String resetOTP(@RequestParam String badgeNumber, RedirectAttributes redirectAttributes)
    {
        System.out.println("request sent to reset OTP :"+badgeNumber);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);




        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber).orElse(null);
        if(employee ==null)
        {
            return "/404";
        }

        if(currentUser.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getRoleName())))
        {
            employee.setOtpCode(null);
            employee.setOtpEnabled(null);
            employeeRepository.save(employee);

            redirectAttributes.addFlashAttribute("sweetMessage", "OTP has been Reset Successfully  ");

            //log the action
            SystemLog systemLog = new SystemLog();
            systemLog.setEmployee(currentUser);
            systemLog.setCreatedAt(LocalDateTime.now());
            systemLog.setDescription("OTP Reset for "+employee.getName() +" Badge#:"+employee.getBadgeNumber());
            systemLogRepository.save(systemLog);
        }
        else
        {
            return "/403";
        }


        return "redirect:/employee/detail?badgeNumber="+employee.getBadgeNumber();
    }// reset otp



    // import photo----------
    @PostMapping("/importPhoto")
    public String importPhoto(@RequestParam MultipartFile photoFile, Model model) {
        String tempDir = "public/upload/temp/";
        String targetDir = "public/images/";

        // Step 1: Create directories
        Path tempPath = Paths.get(tempDir);
        Path targetPath = Paths.get(targetDir);

        try {
            Files.createDirectories(tempPath);
            Files.createDirectories(targetPath);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Error creating directories: " + e.getMessage());
            return "/404";
        }

        // Step 2: Validate file type
        if (!photoFile.getOriginalFilename().endsWith(".zip")) {
            model.addAttribute("message", "Invalid file type. Only ZIP files are allowed.");
            return "/404";
        }

        // Step 3: Save the ZIP file to the temp directory
        Path zipFilePath = tempPath.resolve(photoFile.getOriginalFilename());
        try {
            Files.write(zipFilePath, photoFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Error saving ZIP file: " + e.getMessage());
            return "/404";
        }

        // Step 4: Extract ZIP file and process photos
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    // Extract the base file name (e.g., remove "photo/" prefix)
                    String fileName = Paths.get(entry.getName()).getFileName().toString();

                    // Validate image file types
                    if (!fileName.toLowerCase().matches(".*\\.(jpg|jpeg|png)$")) {
                        System.out.println("Skipping non-image file: " + fileName);
                        continue;
                    }

                    // Extract badge number from the file name
                    String badgeNumber = fileName.substring(0, fileName.lastIndexOf('.')).toUpperCase();

                    // Find employee by badge number
                    Optional<Employee> employeeOptional = employeeRepository.findByBadgeNumber(badgeNumber);
                    if (employeeOptional.isPresent()) {
                        Employee employee = employeeOptional.get();

                        // Create target directory for the employee
                        Path employeePhotoPath = targetPath.resolve(badgeNumber + "/" + fileName);
                        Files.createDirectories(employeePhotoPath.getParent());

                        // Save the photo to the target directory
                        try (OutputStream outputStream = Files.newOutputStream(employeePhotoPath)) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zipInputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, len);
                            }
                        }

                        // Update employee's photo path in the database
                        employee.setImageFileName(employee.getBadgeNumber()+".jpg");
                        employeeRepository.save(employee);
                    } else {
                        System.out.println("No employee found for badge number: " + badgeNumber);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Error extracting ZIP file: " + e.getMessage());
            return "/404";
        }

        // Step 5: Clean up temp directory
        try {
            Files.walk(tempPath)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Redirect to employee list
        model.addAttribute("message", "Photos imported successfully.");
        return "redirect:/employee/list";
    }


    //############ termination -------


    @PostMapping("/termination")
    public String termination(@Valid @ModelAttribute TerminationDto terminationDto)
    {
        //https://www.hrsd.gov.sa/ministry-services/services/end-service-benefit-calculator
        Employee employee = employeeRepository.findByBadgeNumber(terminationDto.getBadgeNumber()).orElse(null);
        if(employee == null)
        {
            return "/404";
        }
        EmployeeClearance employeeClearance = new EmployeeClearance();

        employeeClearance.setEmployee(employee);
        employeeClearance.setActualWage(terminationDto.getActualWage());
        employeeClearance.setLastDay(terminationDto.getLastDay());
        employeeClearance.setArticleNumber(terminationDto.getArticleNumber());
        employeeClearance.setHireDate(terminationDto.getHireDate());
        employeeClearance.setTotalDays(terminationDto.getTotalDays());
        employeeClearance.setContractType(terminationDto.getContractType());
        employeeClearance.setCreatedAt(LocalDateTime.now());
        employeeClearance.setRewardAmount(terminationDto.getRewardAmount());

        employee.setStatus("terminated");
        employeeRepository.save(employee);

        employeeClearanceRepository.save(employeeClearance);


        //send notification for HR, IT=support, Department Manager, medical, vehicle,finance
        // each to get note & Signature
        Department department = employee.getDepartment();
        List<Employee> hrEmployees = employeeRepository.findByRoles_RoleName("HR");
        List<Employee> itEmployees = employeeRepository.findByRoles_RoleName("SUPPORT");
        List<Employee> medicalEmployees = employeeRepository.findByRoles_RoleName("MEDICAL");
        List<Employee> vehicleEmployees = employeeRepository.findByRoles_RoleName("VEHICLE");
        List<Employee> financeEmployees = employeeRepository.findByRoles_RoleName("FINANCE");

        Employee directManager = department.getManager();

        //send Notification to direct manager
        Notification notification = new Notification();
        notification.setEmployee(directManager);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSubject("Employee Clearance");
        notification.setDescription("Employee Clearance For:"+employee.getName());
        notification.setPageLink("/clearance/clearanceDetail?id="+employeeClearance.getId());
        notificationRepository.save(notification);

        hrEmployees.forEach(employee1 -> {
            Notification notification1 = new Notification();
            notification1.setEmployee(employee1);
            notification1.setCreatedAt(LocalDateTime.now());
            notification1.setSubject("Employee Clearance");
            notification1.setDescription("Employee Clearance For:"+employee.getName());
            notification1.setPageLink("/clearance/clearanceDetail?id="+employeeClearance.getId());
            notificationRepository.save(notification1);
        });

        itEmployees.forEach(employee1 ->{
            Notification notification1 = new Notification();
            notification1.setEmployee(employee1);
            notification1.setCreatedAt(LocalDateTime.now());
            notification1.setSubject("Employee Clearance");
            notification1.setDescription("Employee Clearance For:"+employee.getName());
            notification1.setPageLink("/clearance/clearanceDetail?id="+employeeClearance.getId());
            notificationRepository.save(notification1);
        } );

        medicalEmployees.forEach(employee1 -> {
            Notification notification1 = new Notification();
            notification1.setEmployee(employee1);
            notification1.setCreatedAt(LocalDateTime.now());
            notification1.setSubject("Employee Clearance");
            notification1.setDescription("Employee Clearance For:"+employee.getName());
            notification1.setPageLink("/clearance/clearanceDetail?id="+employeeClearance.getId());
            notificationRepository.save(notification1);
        });

        vehicleEmployees.forEach(employee1 -> {
            Notification notification1 = new Notification();
            notification1.setEmployee(employee1);
            notification1.setCreatedAt(LocalDateTime.now());
            notification1.setSubject("Employee Clearance");
            notification1.setDescription("Employee Clearance For:"+employee.getName());
            notification1.setPageLink("/clearance/clearanceDetail?id="+employeeClearance.getId());
            notificationRepository.save(notification1);
        });

        financeEmployees.forEach(employee1 -> {
            Notification notification1 = new Notification();
            notification1.setEmployee(employee1);
            notification1.setCreatedAt(LocalDateTime.now());
            notification1.setSubject("Employee Clearance");
            notification1.setDescription("Employee Clearance For:"+employee.getName());
            notification1.setPageLink("/clearance/clearanceDetail?id="+employeeClearance.getId());
            notificationRepository.save(notification1);
        });




        return "redirect:/clearance/list";
    }




}
