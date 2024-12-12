package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.dto.PurchaseOrderDto;
import com.alhashim.oneIT.dto.VendorDto;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.PurchaseOrder;
import com.alhashim.oneIT.models.PurchaseOrderLine;
import com.alhashim.oneIT.models.Vendor;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.PurchaseOrderRepository;
import com.alhashim.oneIT.repositories.PurchaseOrderRepositoryLine;
import com.alhashim.oneIT.repositories.VendorRepository;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/procurement")
public class ProcurementController {


    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    PurchaseOrderRepositoryLine purchaseOrderRepositoryLine;

    @Autowired
    VendorRepository vendorRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @GetMapping("/order")
    public String orderList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<PurchaseOrder> purchaseOrderPage;

        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            purchaseOrderPage = purchaseOrderRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all  with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            purchaseOrderPage = purchaseOrderRepository.findAll(pageable);
        }


        model.addAttribute("orders", purchaseOrderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", purchaseOrderPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", purchaseOrderPage.getTotalElements());
        model.addAttribute("pageTitle","Purchase Order List");


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +" | "+currentUser.getName();
        model.addAttribute("loginUser", loginUser);

        return "/procurement/orderList";
    } // po list

    @GetMapping("/addPO")
    public String addPOPage(Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);
        String loginUser = currentUser.getBadgeNumber() +" | "+currentUser.getName();
        model.addAttribute("loginUser", loginUser);

        List<Vendor> vendors = vendorRepository.findAll();

        model.addAttribute("vendors", vendors);



        return "/procurement/addPO";
    } //GET Add PO

    @PostMapping("/addPO")
    public String addPO(@Valid @ModelAttribute PurchaseOrderDto purchaseOrderDto, BindingResult result, Model model) {

        if(result.hasErrors())
        {
            model.addAttribute("errors", result.getAllErrors());
            return "/procurement/addPO";
        }

         System.out.println("Create New Purchase Order..................................");
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = employeeRepository.findByBadgeNumber(authentication.getName()).orElse(null);

        if (currentUser == null) {
            // Handle case where user is not found
            return "/404";
        }

        // Log incoming data for debugging
        System.out.println("PurchaseOrder : TotalPriceWithVAT: " + purchaseOrderDto.getTotalPriceWithVAT());
        System.out.println("Lines: " + purchaseOrderDto.getLines().size());

        // Create and populate the PurchaseOrder entity
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setCreatedBy(currentUser);
        purchaseOrder.setCreatedAt(LocalDateTime.now());
        purchaseOrder.setTotalPriceWithVAT(purchaseOrderDto.getTotalPriceWithVAT());

        // Find and set the Vendor
        Vendor vendor = vendorRepository.findById(purchaseOrderDto.getVendorId()).orElse(null);
        if (vendor == null) {
            return "/404";
        }
        purchaseOrder.setVendor(vendor);

        // Process and save PurchaseOrderLines
        List<PurchaseOrderLine> orderLines = purchaseOrderDto.getLines().stream().map(lineDto -> {
            PurchaseOrderLine line = new PurchaseOrderLine();
            line.setDescription(lineDto.getDescription());
            line.setQuantity(lineDto.getQuantity());
            line.setUnitPrice(lineDto.getUnitPrice());
            line.setPercentageVAT(lineDto.getPercentageVAT());
            line.setTotalPrice(lineDto.getTotalPrice());
            line.setUnitVAT(lineDto.getUnitVAT());
            line.setTotalVAT(lineDto.getTotalVAT());
            line.setTotalPriceWithVAT(lineDto.getTotalPriceWithVAT());
            line.setPurchaseOrder(purchaseOrder); // Set the parent PurchaseOrder
            return line;
        }).collect(Collectors.toList());

        // Set the lines in the PurchaseOrder and save both
        purchaseOrder.setLines(orderLines);
        purchaseOrderRepository.save(purchaseOrder);

        return "redirect:/procurement/order";
    } //POST PO




    //vendor list
    @GetMapping("/vendor")
    public String vendorList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<Vendor> vendorPage;

        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            vendorPage = vendorRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all  with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            vendorPage = vendorRepository.findAll(pageable);
        }


        model.addAttribute("vendors", vendorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vendorPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", vendorPage.getTotalElements());
        model.addAttribute("pageTitle","Purchase Order List");
        return "/procurement/vendorList";
    } //GET Vendor List



    @GetMapping("/addVendor")
    public String addVendor(Model model)
    {
        VendorDto vendorDto = new VendorDto();
        model.addAttribute("vendorDto",vendorDto);

        return "/procurement/addVendor";
    } // GET add Vendor



    @PostMapping("/addVendor")
    public String addVendorDo(@Valid @ModelAttribute VendorDto vendorDto, BindingResult result, RedirectAttributes redirectAttributes, Model model)
    {
        if(result.hasErrors())
        {
            return "/procurement/addVendor";
        }
        Vendor vendor = new Vendor();
        vendor.setName(vendorDto.getName());
        vendor.setIban(vendorDto.getIban());
        vendor.setAddress(vendorDto.getAddress());
        vendor.setWebsite(vendorDto.getWebsite());
        vendor.setBankName(vendorDto.getBankName());
        vendor.setArName(vendorDto.getArName());
        vendor.setTaxNumber(vendorDto.getTaxNumber());
        vendor.setRegistrationNumber(vendorDto.getRegistrationNumber());

        vendorRepository.save(vendor);
        redirectAttributes.addFlashAttribute("sweetMessage", "The Vendor has been added Successfully");
        return "redirect:/procurement/vendor";
    } // POST Add Vendor






}
