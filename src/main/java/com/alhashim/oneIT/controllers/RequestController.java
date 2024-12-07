package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.models.*;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.NotificationRepository;
import com.alhashim.oneIT.repositories.RequestRepository;
import com.alhashim.oneIT.repositories.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/request")
public class RequestController {

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    SystemLogRepository systemLogRepository;

    @Autowired
    NotificationRepository notificationRepository;


    String[] categoryList = {"Cable",
                            "Mouse",
                            "Keyboard",
                            "Headset",
                            "Web Cam",
                            "Flash Memory",
                            "External Storage",
                            "Smart Phone",
                            "Printer",
                            "Scanner",
                            "Computer Monitor",
                            "Desktop PC",
                            "Laptop"};

    @GetMapping("/list")
    public String listRequest(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size )
    {
        // list Request only for the creator of the request
        // but if the current user has role of admin or support show all
        // but if the request required manager approval show to manager
        // but if the request required HR approval show to HR

        Page<Request> requestPage;

        //---------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +" | "+currentUser.getName();
        model.addAttribute("loginUser", loginUser);
        //--------
        // is support of admin or hr get all
        boolean isSupportOrAdminOrHR = currentUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName()
                        .equalsIgnoreCase("SUPPORT") || role.getRoleName().equalsIgnoreCase("ADMIN")|| role.getRoleName().equalsIgnoreCase("HR"));

        // is Manager so get all asset under employees who are a member of his department
        boolean isManager = currentUser.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("MANAGER"));

        if(isSupportOrAdminOrHR)
        {
            if(keyword !=null && !keyword.isEmpty())
            {
                // Implement a paginated search query in your repository
                requestPage = requestRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
                model.addAttribute("keyword",keyword);

            }
            else
            {
                // Fetch all  with pagination
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
                requestPage = requestRepository.findAll(pageable);
            }
        }
        else if (isManager)
        {
            if(keyword !=null && !keyword.isEmpty())
            {
                // Implement a paginated search query in your repository
                requestPage = requestRepository.findByKeywordAndDepartment(keyword, currentUser.getDepartment(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
                model.addAttribute("keyword",keyword);

            }
            else
            {
                // Fetch all  with pagination
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
                requestPage = requestRepository.findByRequestedBy_Department(currentUser.getDepartment(), pageable);
            }
        }
        else
        {
            if(keyword !=null && !keyword.isEmpty())
            {
                // Implement a paginated search query in your repository
                requestPage = requestRepository.findByKeywordAndRequestedBy(keyword,currentUser, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
                model.addAttribute("keyword",keyword);

            }
            else
            {
                // Fetch all  with pagination
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
                requestPage = requestRepository.findByRequestedBy(currentUser,pageable);
            }
        }


        model.addAttribute("requests", requestPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requestPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("pageTitle","Request List");
        model.addAttribute("totalItems", requestPage.getTotalElements());




        return "/request/list";
    } // GET List


    @GetMapping("/add")
    public String addRequest(Model model)
    {

        //---------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +" | "+currentUser.getName();
        model.addAttribute("loginUser", loginUser);
        //--------
        model.addAttribute("categoryList", categoryList);
        return "/request/add";
    } //-- GET ADD




    @PostMapping("/add")
    public String addRequestDo(Model model, @RequestParam String category, @RequestParam String justification)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);

        if (!Arrays.asList(categoryList).contains(category))
        {
            return "/404";
        }

        Request request = new Request();
        request.setRequestedBy(currentUser);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        request.setJustification(justification);
        request.setStatus("Waiting Approvals ");


        request.setCategory(category);

        switch (category) {
            case "Cable", "Mouse", "Keyboard", "Headset", "Web Cam" -> {
                request.setRequiredHRApproval(false);
                request.setRequiredManagerApproval(false);
                request.setRequiredAdminApproval(true); // IT Manager
            }
            case "Flash Memory", "External Storage", "Printer", "Scanner", "Computer Monitor", "Desktop PC" -> {
                request.setRequiredHRApproval(false);
                request.setRequiredManagerApproval(true); // Department Manager

                request.setRequiredAdminApproval(true); // IT Manager
            }
            case "Smart Phone", "Laptop" -> {
                request.setRequiredHRApproval(true); //HR

                request.setRequiredManagerApproval(true); // Department Manager

                request.setRequiredAdminApproval(true); // IT Manager
            }
        }




        requestRepository.save(request);

        // Log the  action

        SystemLog systemLog = new SystemLog();
        systemLog.setCreatedAt(LocalDateTime.now());
        systemLog.setEmployee(currentUser);
        systemLog.setDescription("Create New Request IT Asset #:"+request.getId());
        systemLogRepository.save(systemLog);

        // send Notification for reject or approval
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();


        //if the approval is required by admin send him notification
        if(request.getRequiredAdminApproval())
        {
            List<Employee> admins = employeeRepository.findByRoles_RoleName("admin");
            admins.forEach(employee -> {
                Notification notification = new Notification();
                notification.setCreatedAt(LocalDateTime.now());
                notification.setEmployee(employee);
                notification.setSubject("Request IT Asset");

                notification.setDescription("Request Number " + request.getId() +" Need Your Action. for "+request.getRequestedBy().getName());


                notification.setPageLink(baseUrl + "/request/detail?id=" + request.getId());

                notificationRepository.save(notification);
            });
        }

        // if the request required manager approval send him notification
        if(request.getRequiredManagerApproval())
        {
            List<Employee> managers = employeeRepository.findByRoles_RoleName("manager");
            //get only the manager of requester department
            Department requesterDepartment = request.getRequestedBy().getDepartment();
            managers.forEach(manager ->{
                if((manager.getDepartment() == requesterDepartment) && (requesterDepartment !=null && manager.getDepartment() !=null))
                {
                    Notification notification = new Notification();
                    notification.setCreatedAt(LocalDateTime.now());
                    notification.setEmployee(manager);
                    notification.setSubject("Request IT Asset");
                    notification.setDescription("Request Number " + request.getId() +" Need Your Action. for "+request.getRequestedBy().getName());
                    notification.setPageLink(baseUrl + "/request/detail?id=" + request.getId());
                    notificationRepository.save(notification);
                }
            });
        }

        // if the request required HR approval send him notification
        if(request.getRequiredHRApproval())
        {
            List<Employee> hrs = employeeRepository.findByRoles_RoleName("hr");
            hrs.forEach(hr ->{
                Notification notification = new Notification();
                notification.setCreatedAt(LocalDateTime.now());
                notification.setEmployee(hr);
                notification.setSubject("Request IT Asset");
                notification.setDescription("Request Number " + request.getId() +" Need Your Action. for "+request.getRequestedBy().getName());
                notification.setPageLink(baseUrl + "/request/detail?id=" + request.getId());
                notificationRepository.save(notification);
            });
        }

        return "redirect:/request/list";
    } // POST add

    @GetMapping("/detail")
    public String requestDetail(@RequestParam Long id, @RequestParam(required = false) Long nId, Model model)
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);


        if(nId !=null)
        {
            Notification notification = notificationRepository.findById(nId).orElse(null);
            if(notification ==null)
            {
                return "/404";
            }

             if(notification.getEmployee() == currentUser)
             {
                 notification.setReadAt(LocalDateTime.now());
                 notificationRepository.save(notification);
             }
        }

        Request request = requestRepository.findById(id).orElse(null);
        if(request ==null)
        {

            return "/404";
        }
        model.addAttribute("request", request);

        return "/request/detail";
    } // GET Detail


    @GetMapping("/approve")
    public String approveRequest(@RequestParam Long id)
    {
        //check first the current user role
        //also check the required roles
        Request request = requestRepository.findById(id).orElse(null);
        if(request ==null)
        {
            return "/404";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        if(currentUser ==null)
        {
            return "/404";
        }

        //if manager approval required and the current user is manager of the requester then approve
        if(request.getRequiredManagerApproval())
        {
            if(currentUser.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("MANAGER") ))

            {
                if((currentUser.getDepartment() == request.getRequestedBy().getDepartment()) && (request.getRequestedBy().getDepartment() !=null && currentUser.getDepartment() !=null))
                {
                    request.setManagerApproval(true);

                    // Log the  action
                    SystemLog systemLog = new SystemLog();
                    systemLog.setCreatedAt(LocalDateTime.now());
                    systemLog.setEmployee(currentUser);
                    systemLog.setDescription("Response with Approved as Manager for Request IT Asset #"+request.getId());
                    systemLogRepository.save(systemLog);

                }
            }
            else
            {
             System.out.println("Your sent approve for request #"+id+" You are Not Manager for the Requester");
            }
        } // -------Manager Approval

        // if the request required admin approval and the current user is admin then approved
        if(request.getRequiredAdminApproval())
        {
            if(currentUser.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("ADMIN") ))
            {
                request.setAdminApproval(true);

                // Log the  action
                SystemLog systemLog = new SystemLog();
                systemLog.setCreatedAt(LocalDateTime.now());
                systemLog.setEmployee(currentUser);
                systemLog.setDescription("Response with Approved as Admin for Request IT Asset #"+request.getId());
                systemLogRepository.save(systemLog);
            }
        }

        // if the request required HR approval and the current user is a member of HR then approved
        if(request.getRequiredHRApproval())
        {
            if(currentUser.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("HR") ))
            {
                request.setHrApproval(true);

                // Log the  action
                SystemLog systemLog = new SystemLog();
                systemLog.setCreatedAt(LocalDateTime.now());
                systemLog.setEmployee(currentUser);
                systemLog.setDescription("Response with Approved as HR for Request IT Asset #"+request.getId());
                systemLogRepository.save(systemLog);
            }
        }

        //check all required approval if all true update the status to approved if one false set to rejected
        //-------------
                    boolean allApprovalsGranted = true;
                    boolean anyApprovalPending = false; // Track if any required approval is still pending

                    if (Boolean.TRUE.equals(request.getRequiredManagerApproval())) {
                        if (request.getManagerApproval() == null) {
                            anyApprovalPending = true; // Mark as pending if not yet set
                        } else {
                            allApprovalsGranted = allApprovalsGranted && Boolean.TRUE.equals(request.getManagerApproval());
                        }
                    }

                    if (Boolean.TRUE.equals(request.getRequiredAdminApproval())) {
                        if (request.getAdminApproval() == null) {
                            anyApprovalPending = true; // Mark as pending if not yet set
                        } else {
                            allApprovalsGranted = allApprovalsGranted && Boolean.TRUE.equals(request.getAdminApproval());
                        }
                    }

                    if (Boolean.TRUE.equals(request.getRequiredHRApproval())) {
                        if (request.getHrApproval() == null) {
                            anyApprovalPending = true; // Mark as pending if not yet set
                        } else {
                            allApprovalsGranted = allApprovalsGranted && Boolean.TRUE.equals(request.getHrApproval());
                        }
                    }

            // Determine the request status based on approvals
                    if (anyApprovalPending) {
                        // Do not set status if any required approval is pending
                        request.setStatus("PENDING");
                    } else if (allApprovalsGranted) {
                        request.setStatus("APPROVED");
                    } else {
                        request.setStatus("REJECTED");
                    }

        //------------
        request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);
        return "redirect:/request/detail?id="+id;
    } // GET Request Approve


}
