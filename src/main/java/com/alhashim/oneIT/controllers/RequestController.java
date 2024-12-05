package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Notification;
import com.alhashim.oneIT.models.Request;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.NotificationRepository;
import com.alhashim.oneIT.repositories.RequestRepository;
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

        model.addAttribute("requests", requestPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requestPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("pageTitle","Request List");
        model.addAttribute("totalItems", requestPage.getTotalElements());


        //---------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +" | "+currentUser.getName();
        model.addAttribute("loginUser", loginUser);
        //--------

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

        // send Notification for reject or approval
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        if(request.getRequiredAdminApproval())
        {
            List<Employee> admins = employeeRepository.findByRoles_RoleName("admin");
            admins.forEach(employee -> {
                Notification notification = new Notification();
                notification.setCreatedAt(LocalDateTime.now());
                notification.setEmployee(employee);
                notification.setSubject("Request IT Asset");

                notification.setDescription("Request Number " + request.getId() +
                                            " Need Your Action. for "+request.getRequestedBy().getName());

                notification.setPageLink(baseUrl + "/request/detail?id=" + request.getId());

                notificationRepository.save(notification);
            });
        }

        return "redirect:/request/list";
    } // POST add

    @GetMapping("/detail")
    public String requestDetail(@RequestParam Long id, @RequestParam(required = false) Long nId)
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
        return "/request/detail";
    }


}
