package com.alhashim.oneIT.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OtpVerificationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {



        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // If authenticated, check for OTP verification in session
        Boolean otpVerified = (Boolean) httpRequest.getSession().getAttribute("otpVerified");

        System.out.println("Filter Session otpVerified: " + otpVerified);


        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // Get the query string (if any)
        String queryString = httpRequest.getQueryString();
        String fullUrl = requestURI + (queryString != null ? "?" + queryString : "");

        System.out.println("OtpVerificationFilter Class: You Request to URl: "+requestURI);
        // here the first request so save it
        //getSession().setAttribute("targetUrl", requestURI);
        if(((HttpServletRequest) request).getSession().getAttribute("targetUrl")==null)
        {
            System.out.println("request.getSession is null set to: "+requestURI);
            ((HttpServletRequest) request).getSession().setAttribute("targetUrl", fullUrl);
        }


        // Check if the requested resource is public
        if (isPublicResource(requestURI, contextPath)) {
            System.out.println("Allow access to public resources");
            chain.doFilter(request, response); // Allow access to public resources
            return;
        }

            // Check if the user is authenticated
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Check if the user is authenticated:" +authentication);
            System.out.println("session :="+ ((HttpServletRequest) request).getSession().getId());
            if (authentication == null || !authentication.isAuthenticated()) {
                // Redirect unauthenticated users to the login page
                System.out.println("Not authenticated....!");
                httpResponse.sendRedirect(contextPath + "/login");
                return;
            }



                if (otpVerified == null || !otpVerified) {
                    // Allow access to the OTP page only
                    if (requestURI.equals(contextPath + "/otp")) {
                        System.out.println("otpVerified Not yet");
                        chain.doFilter(request, response); // Proceed with the OTP page
                        return;
                    }

                    // Redirect to the login then otp page for any other protected resource
                    httpResponse.sendRedirect(contextPath + "/login");
                    return;
                }

        // If authenticated and OTP verified, continue with the filter chain
        chain.doFilter(request, response);
    }

    /**
     * Check if the requested resource is public and does not require authentication or OTP.
     */
    private boolean isPublicResource(String uri, String contextPath) {
        return uri.equals(contextPath + "/login") || // Login page
                uri.startsWith(contextPath + "/css/") || // Static CSS files
                uri.startsWith(contextPath + "/js/") || // Static JavaScript files
                uri.startsWith(contextPath + "/img/") || // Static images
                uri.startsWith(contextPath + "/otp") ||
                uri.startsWith(contextPath + "/android/oneIT.apk") ||
                uri.startsWith(contextPath + "/otp-setup") ||
                uri.startsWith(contextPath + "/public/") || // Any other public endpoints
                uri.startsWith(contextPath + "/api/verify-totp") ||
                uri.startsWith(contextPath+ "/api/login");
    }
}