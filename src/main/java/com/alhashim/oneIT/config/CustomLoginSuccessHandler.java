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
        // Get the currently authenticated username
        String badgeNumber = authentication.getName();

        // Retrieve the Employee entity using the badge number
        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber)
                .orElseThrow(() -> new RuntimeException("Employee not found with badge number: " + badgeNumber));

        // Log the login action
        SystemLog systemLog = new SystemLog();
        systemLog.setCreatedAt(LocalDateTime.now());
        systemLog.setEmployee(employee);
        systemLog.setDescription("Login");
        systemLogRepository.save(systemLog);

        // Retrieve the original URL
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request, response, targetUrl);
        } else {
            try {
                super.onAuthenticationSuccess(request, response, authentication);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
    }
}