package com.alhashim.oneIT.models;

import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serialNumber;

    private String manufacture;

    private String status;

    private String category;

    private String description;

    private BigDecimal purchasePrice;

    @OneToOne
    @JoinColumn(name = "invoice_id") // Maps the foreign key column
    private Invoice invoice;

    @OneToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    private Date acquisitionDate;


    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee user; // Single user per device


    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
