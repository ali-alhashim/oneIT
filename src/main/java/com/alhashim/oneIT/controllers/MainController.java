package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.config.SecurityConfig;
import com.alhashim.oneIT.dto.ChangePasswordDto;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.SystemLog;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller

public class MainController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({"/dashboard","/",""})
    public String dashboardPage(Model model)
    {
        String badgeNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber).orElse(null);
        System.out.println("Welcome to Dashboard Page badgeNumber: " + badgeNumber);
        assert employee != null;
        System.out.println("Welcome to Dashboard Page Name: " + employee.getName());




        //---------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);

        String loginUser = currentUser.getBadgeNumber() +" | "+currentUser.getName();

        model.addAttribute("loginUser", loginUser);
        //--------

           ChangePasswordDto changePasswordDto = new ChangePasswordDto();

           model.addAttribute("changePasswordDto",changePasswordDto);

            model.addAttribute("badgeNumber", badgeNumber);
            model.addAttribute("userName", employee.getName());
            model.addAttribute("userArName", employee.getArName());
            model.addAttribute("imageFileName", employee.getImageFileName());
            model.addAttribute("workEmail", employee.getWorkEmail());
            model.addAttribute("workMobile",employee.getWorkMobile());
            model.addAttribute("personalMobile", employee.getPersonalMobile());

            model.addAttribute("notificationList", employee.getNotifications());

            if(employee.getDepartment() !=null)
            {
                model.addAttribute("departmentName", employee.getDepartment().getName());
            }
            else
            {
                model.addAttribute("departmentName", "No department");
            }

            model.addAttribute("roles", employee.getRoles());
            model.addAttribute("pageTitle","Dashboard");



            return "dashboard";



    }

    @GetMapping("/login")
    public String loginPage(Model model)
    {

        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage(Model model)
    {
        //clear the session
        return "logout";
    }

    @PostMapping("/changePassword")
    public String changePassword(ChangePasswordDto changePasswordDto, RedirectAttributes redirectAttributes)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);

        if(currentUser !=null)
        {

           if( passwordEncoder.matches(changePasswordDto.getOldPassword(), currentUser.getPassword()))
           {
               currentUser.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
               System.out.println("Password has been updated");


               //----- Log change password action
               SystemLog systemLog = new SystemLog();
               systemLog.setCreatedAt(LocalDateTime.now());
               systemLog.setEmployee(currentUser);
               systemLog.setDescription("Change password");
               systemLogRepository.save(systemLog);


               employeeRepository.save(currentUser);
               redirectAttributes.addFlashAttribute("sweetMessage", "Password has been updated");
           }
           else
           {
               redirectAttributes.addFlashAttribute("sweetMessage", "Current Password Not Correct!");
               System.out.println("the old password not match with current password !");
           }
        }
        else
        {
            redirectAttributes.addFlashAttribute("sweetMessage", "No Employee Found!");
            System.out.println("No Employee");
        }


        return "redirect:/dashboard";
    }


    @GetMapping("/403")
    public String accessDenied() {
        return "403"; // This maps to 403.html in the templates folder
    }


}
