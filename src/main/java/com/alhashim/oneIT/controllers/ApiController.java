package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.config.OtpValidator;
import com.alhashim.oneIT.dto.ApiCheckInOutDto;
import com.alhashim.oneIT.dto.ApiLoginRequestDto;
import com.alhashim.oneIT.dto.ApiTotpCodeDto;
import com.alhashim.oneIT.dto.EmployeeCalendarDto;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.EmployeeCalendar;
import com.alhashim.oneIT.models.Geolocation;
import com.alhashim.oneIT.models.SystemLog;
import com.alhashim.oneIT.repositories.EmployeeCalendarRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.GeolocationRepository;
import com.alhashim.oneIT.repositories.SystemLogRepository;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ApiLoginRequestDto loginRequest, HttpServletRequest request) {
        String badgeNumber = loginRequest.getBadgeNumber();
        String password = loginRequest.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(badgeNumber, password)
            );

            // Set Authentication in SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Persist SecurityContext to Session
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            session.setAttribute("badgeNumber", badgeNumber);
            session.setMaxInactiveInterval(30 * 60);

            return ResponseEntity.ok()
                    .header("Set-Cookie", "JSESSIONID=" + session.getId() + "; HttpOnly; SameSite=Strict")
                    .body(Map.of("message", "Login successful", "badgeNumber", badgeNumber));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request)
    {
        System.out.println("Api Logout");
        HttpSession session = request.getSession(false);  // Don't create session if none exists
        if (session != null) {
            session.invalidate();
        }

        // Invalidate the cookie by setting an empty value and immediate expiration
        return ResponseEntity.ok().body(Map.of("message", "Logout successful"));


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

        if(employee.getOtpCode() ==null)
        {
            return ResponseEntity.status(401).body(Map.of("message", "MFA Not Active open oneIT Account to Activate MFA"));
        }

        // Verify the TOTP code using your OtpValidator
        if (OtpValidator.validateOtp(employee.getOtpCode(), totpCode.getTotpCode())) {
            // TOTP is valid, set otpVerified in the session
            session.setAttribute("otpVerified", true);

            System.out.println("Mark session otpVerified: "+session.getId());

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
        System.out.println("******** CheckIn********* Call");

        HttpSession session = request.getSession(false); // Retrieve the existing session

        if (session == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Session expired. Please log in."));
        }

        System.out.println("CheckIn Session := "+session.getId());
        System.out.println("checkInOutDto BadgeNumber : " + checkInOutDto.getBadgeNumber());




        Employee employee = employeeRepository.findByBadgeNumber(checkInOutDto.getBadgeNumber()).orElse(null);
        if(employee ==null)
        {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid session. Please log in again."));
        }



        //check if  latitude & longitude in approved boundary
        Geolocation geolocation = geolocationRepository.findByCoordinates(checkInOutDto.getLatitude(), checkInOutDto.getLongitude()).orElse(null);
        if(geolocation == null)
        {
            //log the action
            SystemLog systemLog = new SystemLog();
            systemLog.setEmployee(employee);
            systemLog.setCreatedAt(LocalDateTime.now());
            systemLog.setDescription("CheckIn  outside the approved boundary Latitude:"
                                      +checkInOutDto.getLatitude() +" Longitude:"+checkInOutDto.getLongitude()
                                      +" Mobile Model:"+checkInOutDto.getMobileModel()
                                      +" Mobile OS:"+checkInOutDto.getMobileOS()
                                    );
            systemLogRepository.save(systemLog);
            System.out.println("Log CheckIn Action outside the approved boundary");

            return ResponseEntity.status(401).body(Map.of("message", "you are outside the approved boundary"));
        }


        //log the action
        SystemLog systemLog = new SystemLog();
        systemLog.setEmployee(employee);
        systemLog.setCreatedAt(LocalDateTime.now());
        systemLog.setDescription("CheckIn Area Name: "+geolocation.getAreaName() +" Latitude:" +checkInOutDto.getLatitude() +" Longitude:"+checkInOutDto.getLongitude());

        systemLogRepository.save(systemLog);
        System.out.println("Log CheckIn Action");



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
        employeeCalendar.setGeolocation(geolocation);
        employeeCalendar.setMobileModel(checkInOutDto.getMobileModel());
        employeeCalendar.setMobileOS(checkInOutDto.getMobileOS());
        employeeCalendarRepository.save(employeeCalendar);

        return  ResponseEntity.ok().body(Map.of("message", "CheckIn Successfully ^_^"));

    }

    @PostMapping("/checkOut")
    public ResponseEntity<?> checkOut(@RequestBody ApiCheckInOutDto checkInOutDto, HttpServletRequest request)
    {
        System.out.println("******** CheckOUT********* Call");

        HttpSession session = request.getSession(false); // Retrieve the existing session

        if (session == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Session expired. Please log in."));
        }

        System.out.println("CheckOUT Session := "+session.getId());
        System.out.println("checkInOutDto BadgeNumber : " + checkInOutDto.getBadgeNumber());

        Employee employee = employeeRepository.findByBadgeNumber(checkInOutDto.getBadgeNumber()).orElse(null);
        if(employee ==null)
        {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid session. Please log in again."));
        }

        //check if  latitude & longitude in approved boundary
        Geolocation geolocation = geolocationRepository.findByCoordinates(checkInOutDto.getLatitude(), checkInOutDto.getLongitude()).orElse(null);
        if(geolocation == null)
        {
            //log the action
            SystemLog systemLog = new SystemLog();
            systemLog.setEmployee(employee);
            systemLog.setCreatedAt(LocalDateTime.now());
            systemLog.setDescription("CheckOUT  outside the approved boundary Latitude:"
                    +checkInOutDto.getLatitude() +" Longitude:"+checkInOutDto.getLongitude()
                    +" Mobile Model:"+checkInOutDto.getMobileModel()
                    +" Mobile OS:"+checkInOutDto.getMobileOS()
            );
            systemLogRepository.save(systemLog);
            System.out.println("Log CheckOUT Action outside the approved boundary");

            return ResponseEntity.status(401).body(Map.of("message", "you are outside the approved boundary"));
        }


        //log the action
        SystemLog systemLog = new SystemLog();
        systemLog.setEmployee(employee);
        systemLog.setCreatedAt(LocalDateTime.now());
        systemLog.setDescription("CheckOUT Area Name: "+geolocation.getAreaName() +" Latitude:" +checkInOutDto.getLatitude() +" Longitude:"+checkInOutDto.getLongitude());
        systemLogRepository.save(systemLog);
        System.out.println("Log CheckOUT Action");


        // check if there is record

        LocalDate today = LocalDate.now();
        EmployeeCalendar existingEmployeeCalendar = employeeCalendarRepository.findByDayDateAndEmployee(employee, today).orElse(null);
        if (existingEmployeeCalendar !=null)
        {
            LocalTime checkInTime = existingEmployeeCalendar.getCheckIn();
            LocalTime checkOutTime = LocalTime.now();

            existingEmployeeCalendar.setCheckOut(checkOutTime);
            existingEmployeeCalendar.setGeolocationOUT(geolocation);
            existingEmployeeCalendar.setMobileModelOUT(checkInOutDto.getMobileModel());
            existingEmployeeCalendar.setMobileOSOUT(checkInOutDto.getMobileOS());

            // we have LocalTime in & out so total Minutes
            if (checkInTime != null && checkOutTime != null) {
                long totalMinutes = Duration.between(checkInTime, checkOutTime).toMinutes();
                existingEmployeeCalendar.setTotalMinutes((int) totalMinutes);  // Assuming you have a field for this
            }

            employeeCalendarRepository.save(existingEmployeeCalendar);
        }
        else
        {
            // no check in only out !
            EmployeeCalendar employeeCalendar = new EmployeeCalendar();
            employeeCalendar.setCreatedAt(LocalDateTime.now());
            employeeCalendar.setEmployee(employee);
            employeeCalendar.setDayDate(LocalDate.now());
            employeeCalendar.setCheckOut(LocalTime.now());
            employeeCalendar.setGeolocationOUT(geolocation);
            employeeCalendar.setMobileModelOUT(checkInOutDto.getMobileModel());
            employeeCalendar.setMobileOSOUT(checkInOutDto.getMobileOS());
            employeeCalendarRepository.save(employeeCalendar);
        }





        return  ResponseEntity.ok().body(Map.of("message", "CheckOUT Successfully See You *__*"));
    }

    @PostMapping("/timesheet")
    public ResponseEntity<?> timesheet30(HttpServletRequest request) {
        System.out.println("**** timeSheet Call ******");

        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Session expired. Please log in."));
        }

        String badgeNumber = (String) session.getAttribute("badgeNumber");
        if (badgeNumber == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Session expired. Please log in."));
        }

        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber).orElse(null);
        if (employee == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid session. Please log in again."));
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        Page<EmployeeCalendar> employeeCalendarPage = employeeCalendarRepository.findByEmployeeFromTo(
                employee,
                startDate,
                endDate,
                PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "id"))
        );

        List<EmployeeCalendarDto> timesheetDto = employeeCalendarPage.getContent()
                .stream()
                .map(EmployeeCalendarDto::new)
                .toList();

        System.out.println("Timesheet DTO content: " + timesheetDto);  // Debug log (DTO toString)

        return ResponseEntity.ok(timesheetDto);  // Return JSON response
    }
}