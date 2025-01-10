package com.alhashim.oneIT.dto;

import com.alhashim.oneIT.models.Employee;
import jakarta.validation.constraints.NotEmpty;

public class DepartmentDto {

    @NotEmpty(message = "The name is required")
    private String name;
    private String arName;
    private String description;
    private String manager; //his badge number
    private Long id;

    private String managerName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArName() {
        return arName;
    }

    public void setArName(String arName) {
        this.arName = arName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
