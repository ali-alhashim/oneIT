package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.config.OtpValidator;
import com.alhashim.oneIT.dto.ApiLoginRequestDto;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ApiLoginRequestDto loginRequest, HttpServletRequest request) {
        // Extract badgeNumber and password from the request
        String badgeNumber = loginRequest.getBadgeNumber();
        String password = loginRequest.getPassword();

        // Find employee by badge number
        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber).orElse(null);

        // Check if employee exists and password matches
        if (employee != null && passwordEncoder.matches(password, employee.getPassword())) {
            // Create or get existing session
            HttpSession session = request.getSession();

            // Set session attributes
            session.setAttribute("badgeNumber", badgeNumber);
            session.setMaxInactiveInterval(30 * 60);  // Set session timeout (30 min)

            // Return a success response with JSESSIONID as a cookie
            return ResponseEntity.ok()
                    .header("Set-Cookie", "JSESSIONID=" + session.getId() + "; HttpOnly; SameSite=Strict")
                    .body(Map.of("message", "Login successful", "badgeNumber", badgeNumber));
        } else {
            // Invalid credentials
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/verify-totp")
    public ResponseEntity<?> verifyTotp(@RequestBody int otp, HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Retrieve the existing session
        if (session == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Session expired. Please log in."));
        }

        String badgeNumber = (String) session.getAttribute("badgeNumber");
        if (badgeNumber == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Session expired. Please log in."));
        }

        // Find employee by badge number
        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber).orElse(null);
        if (employee == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid session. Please log in again."));
        }

        // Verify the TOTP code using your OtpValidator
        if (OtpValidator.validateOtp(employee.getOtpCode(), otp)) {
            // TOTP is valid, set otpVerified in the session
            session.setAttribute("otpVerified", true);

            // Return success response
            return ResponseEntity.ok()
                    .header("Set-Cookie", "JSESSIONID=" + session.getId() + "; HttpOnly; SameSite=Strict")
                    .body(Map.of("message", "OTP verified successfully", "badgeNumber", badgeNumber));
        } else {
            // Invalid TOTP code
            return ResponseEntity.status(403).body(Map.of("error", "Invalid TOTP code"));
        }
    }
}