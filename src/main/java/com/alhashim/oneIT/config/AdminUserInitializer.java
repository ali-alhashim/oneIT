package com.alhashim.oneIT.config;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Role;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@Configuration
public class AdminUserInitializer {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Bean
    public CommandLineRunner initializeAdminUser() {
        return args -> {
            String badgeNumber   = "A0000";
            String adminPassword = "admin";
            String roleName      = "ADMIN";

            // Check if ROLE_ADMIN role exists, if not create it
            Role superAdminRole = roleRepository.findByRoleName(roleName).orElse(null);
            if (superAdminRole == null) {
                superAdminRole = new Role();
                superAdminRole.setRoleName(roleName);
                roleRepository.save(superAdminRole);
                System.out.println("ADMIN role created.");
            }

            // Check if admin user exists
            if (employeeRepository.findByBadgeNumber(badgeNumber).isEmpty()) {
                Employee admin = new Employee();
                admin.setBadgeNumber(badgeNumber);
                admin.setPassword(passwordEncoder.encode(adminPassword));

                // Set additional admin properties if needed
                admin.setName("Admin User");
                admin.setArName("حساب النظام");
                admin.setRoles(Set.of(superAdminRole));
                admin.setCreatedAt(LocalDateTime.now());
                admin.setPersonalMobile("966547078933");
                employeeRepository.save(admin);
                System.out.println("Admin user created with badge number: " + badgeNumber);
            } else {
                System.out.println("Admin user already exists.");
            }
        };
    }
}
