package com.alhashim.oneIT.config;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.SystemLog;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.SystemLogRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;
import org.springframework.security.web.savedrequest.SavedRequest;


import java.io.IOException;
import java.net.URLEncoder;
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

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        String targetUrl = (savedRequest != null) ? savedRequest.getRedirectUrl() : "/";
        System.out.println(employee.getBadgeNumber() +" want to open "+targetUrl);

        if (employee.getOtpCode() == null) {
            // First-time login, generate secret key
            String secretKey = OtpUtil.generateSecretKey();
            employee.setOtpCode(secretKey);
            employee.setOtpEnabled(true);
            employeeRepository.save(employee);

            String otpAuthUrl = OtpUtil.getOtpAuthUrl(secretKey, badgeNumber);

            // Store the target URL in session and redirect to OTP setup page
            request.getSession().setAttribute("targetUrl", targetUrl);
            request.getSession().setAttribute("otpAuthUrl", otpAuthUrl);
            redirectStrategy.sendRedirect(request, response, "/otp-setup");
        } else if (employee.isOtpEnabled()) {
            // OTP is enabled, redirect to OTP verification page
            request.getSession().setAttribute("targetUrl", targetUrl);
            redirectStrategy.sendRedirect(request, response, "/otp");
        } else {
            // No OTP required, redirect to the original URL or default URL
            redirectStrategy.sendRedirect(request, response, targetUrl);
        }
    }
}