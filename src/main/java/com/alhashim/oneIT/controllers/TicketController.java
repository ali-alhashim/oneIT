package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.dto.TicketDto;
import com.alhashim.oneIT.models.Asset;
import com.alhashim.oneIT.models.Device;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Ticket;
import com.alhashim.oneIT.repositories.DeviceRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.TicketRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @GetMapping("/list")
    public String ticketList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        //check the current user
        // if he is a member of the following Roles [SUPPORT, SUPERADMIN] show all
        // if not filter ticket and show only the requested by him
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);

        boolean isSupportOrAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase("SUPPORT") || role.getRoleName().equalsIgnoreCase("ADMIN"));


        Page<Ticket> ticketPage;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));


        if (keyword != null && !keyword.isEmpty()) {
            // Paginated search query
            if (isSupportOrAdmin) {
                ticketPage = ticketRepository.findByKeyword(keyword, pageable);
            } else {
                ticketPage = ticketRepository.findByKeywordAndRequestedBy(keyword, currentUser, pageable);
            }
        } else {
            // Fetch all tickets or filter by the current user
            if (isSupportOrAdmin) {
                ticketPage = ticketRepository.findAll(pageable);
            } else {
                ticketPage = ticketRepository.findByRequestedBy(currentUser, pageable);
            }
        }

        //---------

        String loginUser = currentUser.getBadgeNumber() +" | "+currentUser.getName();
        model.addAttribute("loginUser", loginUser);
        //--------

        model.addAttribute("tickets", ticketPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ticketPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", ticketPage.getTotalElements());
        model.addAttribute("pageTitle","Ticket List");
        return "ticket/list";
    } //GET Ticket List

    @GetMapping("/add")
    public String addTicketPage(Model model)
    {
        TicketDto ticketDto = new TicketDto();
        List<Asset> userAssets;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);

        if(currentUser !=null)
        {
            userAssets = currentUser.getAssets();

            String loginUser = currentUser.getBadgeNumber() +"|"+currentUser.getName();
            model.addAttribute("loginUser", loginUser);
        }
        else
        {
            userAssets = null;
        }

        model.addAttribute("userAssets",userAssets);
        model.addAttribute("ticketDto", ticketDto);

        return "ticket/add";
    } // GET add


    @PostMapping("/add")
    public String addTicket(@Valid @ModelAttribute TicketDto ticketDto, BindingResult result)
    {
        if(result.hasErrors())
        {
            return "/404";
        }

        Ticket ticket = new Ticket();

        Device device = deviceRepository.findBySerialNumber(ticketDto.getSerialNumber()).orElse(null);
        if(device !=null)
        {
            ticket.setDevice(device);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        if(currentUser !=null)
        {
            ticket.setRequestedBy(currentUser);
            ticket.setSubject(ticketDto.getSubject());
            ticket.setDescription(ticketDto.getDescription());
            ticket.setPriority(ticketDto.getPriority());

            ticketRepository.save(ticket);


            return "redirect:/ticket/list";
        }

        return "/404";

    } //add POST


    @GetMapping("/handle")
    public String handleTicket(@RequestParam Long id, RedirectAttributes redirectAttributes)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);

        boolean isSupportOrAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase("SUPPORT") || role.getRoleName().equalsIgnoreCase("ADMIN"));
        if(isSupportOrAdmin)
        {
            Ticket ticket = ticketRepository.findById(id).orElse(null);

            if(ticket !=null)
            {


                //check the currentUser must be a support or admin else he can't handle


                ticket.setHandledBy(currentUser);
                ticket.setAssignedBy(currentUser);
                ticket.setAssignedDate(LocalDateTime.now());
                ticket.setStatus("In Progress");
                ticketRepository.save(ticket);
                return "redirect:/ticket/list";
            }
            else
            {
                return "/404";
            }
        }
        else
        {
            redirectAttributes.addFlashAttribute("sweetMessage", "You Are Not IT Support or Admin to Handel IT Ticket !");
            return "redirect:/ticket/list";
        }


    } //Handel

    @GetMapping("/detail")
    public String ticketDetail(@RequestParam Long id, RedirectAttributes redirectAttributes, Model model)
    {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if(ticket ==null)
        {
            redirectAttributes.addFlashAttribute("sweetMessage", "Ticket with Id: "+id +" Not Exist !");
            return "redirect:/ticket/list";
        }

        //who open the ticket only support or admin or the creator of the ticket
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        if(currentUser ==null)
        {
            redirectAttributes.addFlashAttribute("sweetMessage", "Please try to login again !");
            return "redirect:/ticket/list";
        }


        boolean isSupportOrAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase("SUPPORT") || role.getRoleName().equalsIgnoreCase("ADMIN"));

        if(!(isSupportOrAdmin || ticket.getRequestedBy() == currentUser))
        {
            redirectAttributes.addFlashAttribute("sweetMessage", "you can't view this ticket only for IT Support or System Admin & the Creator of this Ticket");
            return "redirect:/ticket/list";
        }

        model.addAttribute("ticket", ticket);

        return "/ticket/detail";
    }
}