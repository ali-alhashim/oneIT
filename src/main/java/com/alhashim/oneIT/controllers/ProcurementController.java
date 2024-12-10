package com.alhashim.oneIT.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/procurement")
public class ProcurementController {

    @GetMapping("/order")
    public String orderList()
    {
        return "/procurement/orderList";
    }
}
