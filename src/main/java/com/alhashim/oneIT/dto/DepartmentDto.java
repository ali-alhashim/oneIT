package com.alhashim.oneIT.dto;

import com.alhashim.oneIT.models.Employee;
import jakarta.validation.constraints.NotEmpty;

public class DepartmentDto {

    @NotEmpty(message = "The name is required")
    private String name;
    private String ar_name;
    private String description;
    private String manager; //his badge number

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAr_name() {
        return ar_name;
    }

    public void setAr_name(String ar_name) {
        this.ar_name = ar_name;
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
}
