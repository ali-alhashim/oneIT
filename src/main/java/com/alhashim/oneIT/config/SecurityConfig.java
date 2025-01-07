package com.alhashim.oneIT.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableJpaAuditing
public class SecurityConfig  {

    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    private final OtpVerificationFilter otpVerificationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Ensure the SecurityContext is stored in the session
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }


    public SecurityConfig(CustomLoginSuccessHandler customLoginSuccessHandler, OtpVerificationFilter otpVerificationFilter) {
        this.customLoginSuccessHandler = customLoginSuccessHandler;

        this.otpVerificationFilter = otpVerificationFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        return http

                .requestCache(cache -> cache
                        .requestCache(new HttpSessionRequestCache())
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/otp", "/css/**", "/js/**", "/api/login", "/api/verify-totp", "/android/oneIT.apk").permitAll() // Allow public access to login and static resources
                        .requestMatchers("/employee/list").hasAnyRole("ADMIN","HR","SUPPORT")
                        .requestMatchers("/department/list").hasAnyRole("ADMIN","HR","SUPPORT")
                        .requestMatchers("/payslip/list").hasAnyRole("ADMIN","HR")
                        .requestMatchers("/payslip/add").hasAnyRole("ADMIN","HR")
                        .requestMatchers("/geolocation/list").hasAnyRole("ADMIN","HR")
                        .requestMatchers("/geolocation/add").hasAnyRole("ADMIN","HR")
                        .requestMatchers("/shiftSchedule/add").hasAnyRole("ADMIN","HR")
                        .requestMatchers("/timesheet/list").hasAnyRole("ADMIN","HR","SUPPORT")
                        .requestMatchers("/clearance/list").hasAnyRole("ADMIN","HR","MANAGER","SUPPORT")
                        .requestMatchers("/system/logs").hasAnyRole("ADMIN","HR","SUPPORT")
                        .requestMatchers("/system/roles").hasAnyRole("ADMIN","HR","SUPPORT")
                        .requestMatchers("/system/database").hasAnyRole("ADMIN","HR","SUPPORT")
                        .requestMatchers("/shiftSchedule/shiftScheduleDetail").hasAnyRole("ADMIN","HR","SUPPORT")
                        .requestMatchers("/procurement/**").hasAnyRole("ADMIN","PROCUREMENT")
                        .requestMatchers("/system/databaseBackup").hasAnyRole("ADMIN")
                        .requestMatchers("/system/role/add-employee").hasAnyRole("ADMIN")


                        .anyRequest().authenticated()

                )

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")  // Disable CSRF for APIs
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/403") // Custom 403 Forbidden page
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("badgeNumber") // Custom username parameter
                        .passwordParameter("password") // Custom password parameter
                       // .defaultSuccessUrl("/dashboard") // Redirect to dashboard on successful login
                        .failureUrl("/login?error=true") // Redirect back to login page on failure
                        .successHandler(customLoginSuccessHandler)
                        .permitAll()
                )



                .logout(logout -> logout
                        .logoutUrl("/logout") // Logout URL

                        .logoutSuccessUrl("/login?logout=true") // Redirect to login on successful logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
               .addFilterAfter(otpVerificationFilter, SecurityContextHolderFilter.class) // Apply OTP check before other filters
                .build();
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
