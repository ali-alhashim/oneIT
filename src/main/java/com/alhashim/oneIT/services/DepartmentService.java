package com.alhashim.oneIT.services;

import com.alhashim.oneIT.models.Department;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Set<Employee> searchEmployeesInDepartment(Long departmentId, String keyword) {
        // Fetch the department and its employees
        Department department = departmentRepository.findByIdWithEmployees(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // Filter employees based on the keyword
        return department.getEmployees().stream()
                .filter(employee -> employee.getName().toLowerCase().contains(keyword.toLowerCase())
                        || employee.getArName().toLowerCase().contains(keyword.toLowerCase())
                        || employee.getBadgeNumber().toLowerCase().contains(keyword.toLowerCase())
                        || employee.getWorkMobile().toLowerCase().contains(keyword.toLowerCase())
                        || employee.getWorkEmail().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toSet());
    }
}
