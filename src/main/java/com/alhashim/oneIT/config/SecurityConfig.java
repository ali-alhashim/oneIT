package com.alhashim.oneIT.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.alhashim.oneIT.config.CustomLoginSuccessHandler;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableJpaAuditing
public class SecurityConfig  {

    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    private final OtpVerificationFilter otpVerificationFilter;


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
                        .requestMatchers("/login", "/otp", "/css/**", "/js/**").permitAll() // Allow public access to login and static resources
                        .requestMatchers("/employee/list").hasAnyRole("ADMIN","HR","SUPPORT")
                        .requestMatchers("/department/list").hasAnyRole("ADMIN","HR","SUPPORT")
                        .anyRequest().authenticated()

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
