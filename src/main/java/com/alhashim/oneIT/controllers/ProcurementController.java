package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.dto.*;
import com.alhashim.oneIT.models.*;
import com.alhashim.oneIT.repositories.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    InvoiceRepository invoiceRepository;

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


    @GetMapping("/orderDetail")
    public String orderDetail(@RequestParam Long id, Model model)
    {
        PurchaseOrder order = purchaseOrderRepository.findById(id).orElse(null);
        if(order ==null)
        {
            return "/404";
        }

        model.addAttribute("order", order);
        return "/procurement/orderDetail";
    } // order detail----

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
        purchaseOrder.setStatus(purchaseOrderDto.getStatus());
        purchaseOrder.setDeadLine(purchaseOrderDto.getDeadLine());
        purchaseOrder.setDocumentRef(purchaseOrderDto.getDocumentRef());
        purchaseOrder.setDeliveryAddress(purchaseOrderDto.getDeliveryAddress());
        purchaseOrder.setPaymentTerms(purchaseOrderDto.getPaymentTerms());


        // Find and set the Vendor
        Vendor vendor = vendorRepository.findById(purchaseOrderDto.getVendorId()).orElse(null);
        if (vendor == null) {
            return "/404";
        }
        purchaseOrder.setVendor(vendor);

        //find and set contact
        Contact contact = contactRepository.findById(purchaseOrderDto.getContactId()).orElse(null);
        if(contact !=null)
        {
            purchaseOrder.setContact(contact);
        }

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
        vendor.setCreatedAt(LocalDateTime.now());

        vendorRepository.save(vendor);
        redirectAttributes.addFlashAttribute("sweetMessage", "The Vendor has been added Successfully");
        return "redirect:/procurement/vendor";
    } // POST Add Vendor


    @GetMapping("/vendorDetail")
    public String vendorDetail(@RequestParam Long id, Model model)
    {
        Vendor vendor = vendorRepository.findById(id).orElse(null);
        if(vendor ==null)
        {
            return "/404";
        }

        model.addAttribute("vendor", vendor);
        return "/procurement/vendorDetail";
    } // vendor Detail


    @GetMapping("/editVendor")
    public String editVendor(Model model, @RequestParam Long id)
    {
        VendorDto vendorDto = new VendorDto();
        Vendor vendor = vendorRepository.findById(id).orElse(null);
        if(vendor ==null)
        {
            return "/404";
        }
        vendorDto.setAddress(vendor.getAddress());
        vendorDto.setIban(vendor.getIban());
        vendorDto.setName(vendor.getName());
        vendorDto.setWebsite(vendor.getWebsite());
        vendorDto.setArName(vendor.getArName());
        vendorDto.setBankName(vendor.getBankName());
        vendorDto.setRegistrationNumber(vendor.getRegistrationNumber());
        vendorDto.setTaxNumber(vendor.getTaxNumber());
        vendorDto.setId(vendor.getId());
        model.addAttribute("vendorDto", vendorDto);
        return "/procurement/editVendor";
    } // Edit Vendor


    @PostMapping("/editVendor")
    public String editVendorDo(@Valid @ModelAttribute VendorDto vendorDto)
    {
        Vendor vendor = vendorRepository.findById(vendorDto.getId()).orElse(null);
        if(vendor ==null)
        {
            return "/404";
        }
        vendor.setWebsite(vendorDto.getWebsite());
        vendor.setUpdatedAt(LocalDateTime.now());
        vendor.setArName(vendorDto.getArName());
        vendor.setName(vendorDto.getName());
        vendor.setRegistrationNumber(vendorDto.getRegistrationNumber());
        vendor.setTaxNumber(vendorDto.getTaxNumber());
        vendor.setIban(vendorDto.getIban());
        vendor.setBankName(vendorDto.getBankName());
        vendor.setAddress(vendorDto.getAddress());
        vendorRepository.save(vendor);
        return "redirect:/procurement/vendorDetail?id="+vendorDto.getId();
    } // post edit vendor


    @PostMapping("/addContact")
    public String addContact(@Valid @ModelAttribute ContactDto contactDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("message", result.getAllErrors());
            return "/404"; // Redirect back to form with error details
        }

        Vendor vendor = vendorRepository.findById(contactDto.getVendorId()).orElse(null);
        if (vendor == null) {
            model.addAttribute("message", "Vendor not found.");
            return "/404"; // Specific error page
        }

        Contact contact = new Contact();
        contact.setName(contactDto.getName());
        contact.setArName(contactDto.getArName());
        contact.setVendor(vendor);
        contact.setEmail(contactDto.getEmail());
        contact.setMobile(contactDto.getMobile());

        if (!contactDto.getImageFile().isEmpty() && contactDto.getImageFile() != null) {
            String fileType = contactDto.getImageFile().getContentType();
            if (!fileType.equals("image/png") && !fileType.equals("image/jpeg")) {
                model.addAttribute("message", "Only PNG and JPG file types are allowed.");
                return "/404";
            }

            String folderPath = "/uploads/contact/" + contact.getId();
            File folder = new File(folderPath);
            if (!folder.exists()) {
                boolean dirsCreated = folder.mkdirs(); // Create directories
                if (!dirsCreated) {
                    model.addAttribute("message", "Failed to create directory for the file.");
                    return "/404";
                }
            }

            String targetPath = folderPath + "/" + contactDto.getImageFile().getOriginalFilename();
            try {
                File destinationFile = new File(targetPath);
                contactDto.getImageFile().transferTo(destinationFile);
                contact.setImageFileName(contactDto.getImageFile().getOriginalFilename());
            } catch (IOException e) {
                model.addAttribute("message", "Error saving the file.");
                return "/404";
            }
        }

        contactRepository.save(contact);

        List<Contact> listContact = vendor.getRepresentatives();
        if (listContact == null) {
            listContact = new ArrayList<>();
        }
        listContact.add(contact);
        vendor.setRepresentatives(listContact);
        vendor.setUpdatedAt(LocalDateTime.now());
        vendorRepository.save(vendor);

        return "redirect:/procurement/vendorDetail?id=" + contactDto.getVendorId();
    } // add contact




    @GetMapping("/getContactVendor")
    public ResponseEntity<?> getContactVendor(@RequestParam Long id) {
        Vendor vendor = vendorRepository.findById(id).orElse(null);
        if (vendor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
        }

        List<Contact> contacts = contactRepository.findByVendor(vendor);

        // Map contacts to ContactNameIdDto
        List<ContactNameIdDto> contactDTOs = contacts.stream()
                .map(contact -> new ContactNameIdDto(contact.getId(), contact.getName()))
                .toList();

        return ResponseEntity.ok(contactDTOs); // Spring Boot automatically converts to JSON if Jackson is on the classpath
    } // getContactVendor


    @PostMapping("/addOrderInvoice")
    public String addOrderInvoice(@Valid @ModelAttribute AddInvoiceToOrderDto addInvoiceToOrderDto, BindingResult result, Model model)
    {
        if(result.hasErrors())
        {
            model.addAttribute("message", result.getAllErrors());
        }
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(addInvoiceToOrderDto.getPurchaseOrderId()).orElse(null);
        if(purchaseOrder ==null)
        {
            return "/404";
        }

        Vendor vendor = vendorRepository.findById(addInvoiceToOrderDto.getVendorId()).orElse(null);
        if(vendor ==null)
        {
            model.addAttribute("message", "vendor not found");
            return "/404";
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(addInvoiceToOrderDto.getInvoiceNumber());
        invoice.setVendor(vendor);
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setInvoiceDate(addInvoiceToOrderDto.getInvoiceDate());
        invoice.setPurchaseOrder(purchaseOrder);
        invoice.setPaymentMethod(addInvoiceToOrderDto.getPaymentMethod());
        invoice.setStatus(addInvoiceToOrderDto.getStatus());
        invoice.setTotalPrice(addInvoiceToOrderDto.getTotalPrice());
        invoice.setTotalVAT(addInvoiceToOrderDto.getTotalVAT());
        invoice.setTotalPriceWithVAT(addInvoiceToOrderDto.getTotalPriceWithVAT());

        //set all lines to invoice-------------------------------
        // Process and save invoiceLines
        List<InvoiceLine> invoiceLines = addInvoiceToOrderDto.getLines().stream().map(lineDto -> {
            InvoiceLine line = new InvoiceLine();
            line.setDescription(lineDto.getDescription());
            line.setQuantity(lineDto.getQuantity());
            line.setUnitPrice(lineDto.getUnitPrice());
            line.setPercentageVAT(lineDto.getPercentageVAT());
            line.setTotalPrice(lineDto.getTotalPrice());
            line.setUnitVAT(lineDto.getUnitVAT());
            line.setTotalVAT(lineDto.getTotalVAT());
            line.setTotalPriceWithVAT(lineDto.getTotalPriceWithVAT());
            line.setInvoice(invoice); // Set the parent PurchaseOrder
            return line;
        }).collect(Collectors.toList());

        // Set the lines in the PurchaseOrder and save both
        invoice.setLines(invoiceLines);
        invoice.setUpdatedAt(LocalDateTime.now());


        //---------------------------------------------------------

        if(!addInvoiceToOrderDto.getPdfFile().isEmpty())
        {

                    MultipartFile pdfFile = addInvoiceToOrderDto.getPdfFile();
                    LocalDateTime createdAt = LocalDateTime.now();
                    String storageFileName = createdAt.toEpochSecond(ZoneOffset.UTC) + "_" + pdfFile.getOriginalFilename();
                    String uploadDir = "public/upload/invoice/" + invoiceRepository.count() + "/";
                    Path uploadPath = Paths.get(uploadDir);

                    try {
                        if(!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }

                        InputStream inputStream = pdfFile.getInputStream();
                        Path filePath = Paths.get(uploadDir + storageFileName);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

                        // Set the stored filename in the invoice
                        invoice.setPdfFileName(storageFileName);
                    } catch (Exception e) {
                        System.out.println("Saving PDF file exception: " + e.getMessage());
                        result.addError(new FieldError("addInvoiceToOrderDto", "pdfFile", e.getMessage()));
                        return "invoice/add";
                    }
        }

        invoiceRepository.save(invoice);

        return "redirect:/procurement/orderDetail?id="+addInvoiceToOrderDto.getPurchaseOrderId();

    } // addOrderInvoice



    @GetMapping("/invoice")
    public String invoiceList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {

        Page<Invoice> invoicePage;

        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            invoicePage = invoiceRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all  with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            invoicePage = invoiceRepository.findAll(pageable);
        }


        model.addAttribute("invoices", invoicePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", invoicePage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", invoicePage.getTotalElements());
        model.addAttribute("pageTitle","Invoice List");
        return "/procurement/invoiceList";
    }






}
