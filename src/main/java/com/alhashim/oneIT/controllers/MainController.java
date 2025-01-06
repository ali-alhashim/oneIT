package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.config.OtpValidator;
import com.alhashim.oneIT.config.SecurityConfig;
import com.alhashim.oneIT.dto.ChangePasswordDto;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Notification;
import com.alhashim.oneIT.models.SystemLog;
import com.alhashim.oneIT.repositories.DeviceRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.NotificationRepository;
import com.alhashim.oneIT.repositories.SystemLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller

public class MainController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @GetMapping({"/dashboard","/",""})
    public String dashboardPage(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size)
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
            model.addAttribute("employeeId", employee.getId());
            model.addAttribute("userName", employee.getName());
            model.addAttribute("userArName", employee.getArName());
            model.addAttribute("imageFileName", employee.getImageFileName());
            model.addAttribute("workEmail", employee.getWorkEmail());
            model.addAttribute("workMobile",employee.getWorkMobile());
            model.addAttribute("personalMobile", employee.getPersonalMobile());
            model.addAttribute("personalEmail", employee.getPersonalEmail());
            model.addAttribute("emergencyContactName", employee.getEmergencyContactName());
            model.addAttribute("emergencyContactMobile", employee.getEmergencyContactMobile());

            int laptopCount = deviceRepository.countByEmployeeAndCategory(employee, "Laptop");
            model.addAttribute("laptopCount",laptopCount);

            int smartPhoneCount = deviceRepository.countByEmployeeAndCategory(employee, "SmartPhone");
            model.addAttribute("smartPhoneCount",smartPhoneCount);

            int monitorCount = deviceRepository.countByEmployeeAndCategory(employee, "Monitor");
            model.addAttribute("monitorCount",monitorCount);

            int printerCount = deviceRepository.countByEmployeeAndCategory(employee, "Printer");
            model.addAttribute("printerCount",printerCount);



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

         Page<Notification> notificationPage;
        if(keyword !=null && !keyword.isEmpty())
        {
            notificationPage = notificationRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            notificationPage = notificationRepository.findByEmployee(employee,pageable);
        }


        model.addAttribute("notificationList", notificationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notificationPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", notificationPage.getTotalElements());




            return "/dashboard";



    }

    @GetMapping("/login")
    public String loginPage(Model model)
    {

        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage(Model model, HttpSession session)
    {
        //clear the session
        if (session != null) {
            session.invalidate(); // Invalidate the session
        }
        return "logout";
    }

    @PostMapping("/updateMyInfo")
    public String updateMyInfo(@RequestParam String personalEmail, @RequestParam String personalMobile, @RequestParam String emergencyContactName, @RequestParam String emergencyContactMobile)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        if(currentUser ==null)
        {
            return "/404";
        }
        currentUser.setPersonalEmail(personalEmail);
        currentUser.setPersonalMobile(personalMobile);
        currentUser.setEmergencyContactName(emergencyContactName);
        currentUser.setEmergencyContactMobile(emergencyContactMobile);
        employeeRepository.save(currentUser);
        return "redirect:/dashboard";
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


    @GetMapping("/otp-setup")
    public String showOtpSetupPage(HttpSession session, Model model) {
        String otpAuthUrl = (String) session.getAttribute("otpAuthUrl");
        model.addAttribute("otpAuthUrl", otpAuthUrl);
        return "/otp-setup"; // Name of the view for OTP setup
    }

    @GetMapping("/otp")
    public String showOtpPage() {
        return "/otp";
    }

    @PostMapping("/otp")
    public void verifyOtp(@RequestParam Integer otp,
                          HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session,
                          Principal principal,
                          RedirectAttributes redirectAttributes) throws IOException {
        Employee employee = employeeRepository.findByBadgeNumber(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));

        System.out.println("Verify OTP for Employee: " + employee.getName());
        System.out.println("Employee OTP Secret: " + employee.getOtpCode());
        System.out.println("Provided OTP: " + otp);

        if (OtpValidator.validateOtp(employee.getOtpCode(), otp)) {
            // OTP is valid
            String targetUrl = (String) session.getAttribute("targetUrl");

            // Remove targetUrl from session to prevent reuse
            session.removeAttribute("targetUrl");

            System.out.println("OTP is valid. Redirecting to: " + (targetUrl != null ? targetUrl : "/dashboard"));

            // Mark session as verified
            session.setAttribute("otpVerified", true);
            System.out.println("Session 'otpVerified' attribute set: " + session.getAttribute("otpVerified"));

            // Save SecurityContext to session to persist authentication
            SecurityContext securityContext = SecurityContextHolder.getContext();
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            // Use HttpServletResponse for more reliable redirection
            if (targetUrl != null && !targetUrl.isEmpty()) {
                response.sendRedirect(targetUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard");
            }
        } else {
            // OTP is invalid
            System.out.println("Invalid OTP!");
            redirectAttributes.addFlashAttribute("error", "Invalid OTP. Please try again.");
            response.sendRedirect(request.getContextPath() + "/otp");
        }
    }

}
