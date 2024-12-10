package com.alhashim.oneIT.controllers;


import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Role;
import com.alhashim.oneIT.models.SystemLog;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.RoleRepository;
import com.alhashim.oneIT.repositories.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/system")
public class SystemController {



    @Autowired
    RoleRepository roleRepository;
    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    SystemLogRepository systemLogRepository;

    @Autowired
    private DataSource dataSource;

    @GetMapping("/roles")
    public String rolesList(Model model)
    {
        List<Role> roles = roleRepository.findAll();

        model.addAttribute("roles", roles);

        return "/system/rolesList";
    }

    @GetMapping("/rolesDetail")
    public String rolesDetail(Model model, @RequestParam Long id, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<Employee> employeePage;
        Role role = roleRepository.findById(id).orElse(null);
       if(role ==null)
       {
           return "/404";
       }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
       employeePage = employeeRepository.findByRoles_RoleName(role.getRoleName(),pageable );

       List<Employee> employees = employeeRepository.findAll();


        model.addAttribute("employeePage", employeePage.getContent());
        model.addAttribute("employees", employees);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", employeePage.getTotalElements());
        model.addAttribute("pageTitle","Role Members List");
        model.addAttribute("role", role);

        return "/system/rolesDetail";
    } //roles Detail


    @GetMapping("/logs")
    public String logsList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<SystemLog> logPage;
        if(keyword !=null && !keyword.isEmpty())
        {
            logPage = systemLogRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            logPage = systemLogRepository.findAll(pageable);
        }


        model.addAttribute("logs", logPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", logPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", logPage.getTotalElements());
        model.addAttribute("pageTitle","Logs List");

        return "/system/logsList";
    } // logs

    @GetMapping("/database")
    public String database(Model model) {
        String databaseName = "";
        String databaseSize = "";
        String uploadsFolderSize = "";

        // Fetch database information
        try (Connection connection = dataSource.getConnection()) {
            databaseName = connection.getCatalog();

            // Fetch the database size (MySQL example)
            String query = "SELECT table_schema AS database_name, " +
                    "SUM(data_length + index_length) AS size_in_bytes " +
                    "FROM information_schema.tables " +
                    "WHERE table_schema = ? " +
                    "GROUP BY table_schema";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, databaseName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        long sizeInBytes = resultSet.getLong("size_in_bytes");
                        databaseSize = String.format("%.2f MB", sizeInBytes / (1024.0 * 1024.0));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            databaseName = databaseName.isEmpty() ? "Error fetching database name" : databaseName;
            databaseSize = "Error fetching database size";
        }

        // Calculate the size of the uploads folder
        try {
            Path uploadsPath = Paths.get("public");
            uploadsFolderSize = getFolderSize(uploadsPath);
        } catch (IOException e) {
            e.printStackTrace();
            uploadsFolderSize = "Error calculating uploads folder size";
        }

        // Add attributes to the model
        model.addAttribute("databaseName", databaseName);
        model.addAttribute("databaseSize", databaseSize);
        model.addAttribute("uploadsFolderSize", uploadsFolderSize);

        return "/system/database";
    }

    // Helper method to calculate folder size
    private String getFolderSize(Path folderPath) throws IOException {
        long sizeInBytes = Files.walk(folderPath)
                .filter(Files::isRegularFile)
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return 0L;
                    }
                }).sum();

        return String.format("%.2f MB", sizeInBytes / (1024.0 * 1024.0));
    }
}
