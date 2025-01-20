package com.alhashim.oneIT.config;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.SystemLog;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.SystemLogRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SystemLogRepository systemLogRepository;
    private final EmployeeRepository employeeRepository;

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public CustomLoginSuccessHandler(SystemLogRepository systemLogRepository, EmployeeRepository employeeRepository) {
        this.systemLogRepository = systemLogRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String badgeNumber = authentication.getName();

        // Retrieve the Employee entity
        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found with badge number: " + badgeNumber));

        // Log the login action
        SystemLog systemLog = new SystemLog();
        systemLog.setCreatedAt(LocalDateTime.now());
        systemLog.setEmployee(employee);
        systemLog.setDescription("Login");
        systemLogRepository.save(systemLog);

        String sessionTargetUrl =  request.getSession().getAttribute("targetUrl").toString();

        //SavedRequest savedRequest = requestCache.getRequest(request, response);
        String defaultTargetUrl = "/dashboard"; // Default fallback

        String targetUrl = (sessionTargetUrl != null) ? sessionTargetUrl : defaultTargetUrl;

        System.out.println("onAuthenticationSuccess class SavedRequested =  "+targetUrl);





        // Handle OTP logic
        if (employee.getOtpCode() == null) {
            // First-time login, generate secret key
            String secretKey = OtpUtil.generateSecretKey();
            employee.setOtpCode(secretKey);
            employee.setOtpEnabled(true);
            employeeRepository.save(employee);

            String otpAuthUrl = OtpUtil.getOtpAuthUrl(secretKey, badgeNumber);
            request.getSession().setAttribute("otpAuthUrl", otpAuthUrl);

            //request.getSession().setAttribute("targetUrl", targetUrl);
            System.out.println("onAuthenticationSuccess Handle OTP logic setAttribute targetUrl ="+targetUrl);

            redirectStrategy.sendRedirect(request, response, "/otp-setup");
        } else if (employee.isOtpEnabled()) {

            // Redirect to OTP verification page
            //request.getSession().setAttribute("targetUrl", targetUrl);
            System.out.println("onAuthenticationSuccess Redirect to OTP verification page setAttribute targetUrl = "+targetUrl);

            redirectStrategy.sendRedirect(request, response, "/otp");
        } else {
            // Redirect to the target URL or fallback to default
            redirectStrategy.sendRedirect(request, response, targetUrl);
        }
    }
}