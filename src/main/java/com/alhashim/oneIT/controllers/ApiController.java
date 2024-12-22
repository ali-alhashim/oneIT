package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.config.OtpValidator;
import com.alhashim.oneIT.dto.ApiCheckInOutDto;
import com.alhashim.oneIT.dto.ApiLoginRequestDto;
import com.alhashim.oneIT.dto.ApiTotpCodeDto;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.EmployeeCalendar;
import com.alhashim.oneIT.models.Geolocation;
import com.alhashim.oneIT.models.SystemLog;
import com.alhashim.oneIT.repositories.EmployeeCalendarRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.GeolocationRepository;
import com.alhashim.oneIT.repositories.SystemLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    GeolocationRepository geolocationRepository;

    @Autowired
    SystemLogRepository systemLogRepository;

    @Autowired
    EmployeeCalendarRepository employeeCalendarRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ApiLoginRequestDto loginRequest, HttpServletRequest request) {
        System.out.println("************* Api login request *****************************");
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
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }

    @PostMapping("/verify-totp")
    public ResponseEntity<?> verifyTotp(@RequestBody ApiTotpCodeDto totpCode, HttpServletRequest request) {
        System.out.println("***** api verify-totp******* : totpCode: " + totpCode.getTotpCode());

        HttpSession session = request.getSession(false); // Retrieve the existing session
        if (session == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Session expired. Please log in."));
        }

        String badgeNumber = (String) session.getAttribute("badgeNumber");
        if (badgeNumber == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Session expired. Please log in."));
        }

        // Find employee by badge number
        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber).orElse(null);
        if (employee == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid session. Please log in again."));
        }

        // Verify the TOTP code using your OtpValidator
        if (OtpValidator.validateOtp(employee.getOtpCode(), totpCode.getTotpCode())) {
            // TOTP is valid, set otpVerified in the session
            session.setAttribute("otpVerified", true);

            // Return success response
            return ResponseEntity.ok()
                    .header("Set-Cookie", "JSESSIONID=" + session.getId() + "; HttpOnly; SameSite=Strict")
                    .body(Map.of("message", "OTP verified successfully",
                                 "badgeNumber", badgeNumber,
                                 "name", employee.getName()
                                ));
        } else {
            // Invalid TOTP code
            return ResponseEntity.status(403).body(Map.of("message", "Invalid TOTP code"));
        }
    }

    @PostMapping("/checkIn")
    public ResponseEntity<?> checkIn(@RequestBody ApiCheckInOutDto checkInOutDto, HttpServletRequest request)
    {

        Employee employee = employeeRepository.findByBadgeNumber(checkInOutDto.getBadgeNumber()).orElse(null);
        if(employee ==null)
        {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid session. Please log in again."));
        }



        //check if  latitude & longitude in approved boundary
        Geolocation geolocation = geolocationRepository.findAreaNameByCoordinates(checkInOutDto.getLatitude(), checkInOutDto.getLongitude()).orElse(null);
        if(geolocation == null)
        {
            return ResponseEntity.status(401).body(Map.of("message", "you are outside the approved boundary"));
        }


        //log the action
        SystemLog systemLog = new SystemLog();
        systemLog.setEmployee(employee);
        systemLog.setCreatedAt(LocalDateTime.now());
        systemLog.setDescription("CheckIn Area Name: "+geolocation.getAreaName());
        systemLogRepository.save(systemLog);



        // Check if the employee has already checked in today
        LocalDate today = LocalDate.now();
        Optional<EmployeeCalendar> existingCheckIn = employeeCalendarRepository.findByDayDateAndEmployee(employee, today);
        if (existingCheckIn.isPresent())
        {
            return ResponseEntity.status(400).body(Map.of("message", "Already checked in today"));
        }




        EmployeeCalendar employeeCalendar = new EmployeeCalendar();
        employeeCalendar.setCreatedAt(LocalDateTime.now());
        employeeCalendar.setEmployee(employee);
        employeeCalendar.setDayDate(LocalDate.now());
        employeeCalendar.setCheckIn(LocalTime.now());
        employeeCalendar.setMobileModel(checkInOutDto.getMobileModel());
        employeeCalendar.setMobileOS(checkInOutDto.getMobileOS());
        employeeCalendarRepository.save(employeeCalendar);

        return  ResponseEntity.ok().body(Map.of("message", "CheckIn Successfully"));

    }
}