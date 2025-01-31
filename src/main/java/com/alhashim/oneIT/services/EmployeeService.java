package com.alhashim.oneIT.services;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import com.alhashim.oneIT.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TicketRepository ticketRepository;




    @Override
    public UserDetails loadUserByUsername(String badgeNumber) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByBadgeNumber(badgeNumber).orElse(null);
        if(employee !=null)
        {
            initializeRoles(employee);

            String[] roles = employee.getRoles().stream().map(role -> role.getRoleName()).toArray(String[]::new);

            var springUser = User.withUsername(employee.getBadgeNumber())
                    .password(employee.getPassword())
                    .roles(roles)
                    .build();

            System.out.println("Employee with badge number: " + badgeNumber +" login");
            return springUser;
        }
        System.out.println("Employee not found with badge number: " + badgeNumber);
        return null;
    }

    private void initializeRoles(Employee employee) {
        employee.getRoles().size(); // This triggers lazy loading of roles
    }


    public List<Employee> getAllSupportEmployees() {
        List<Employee> employees = employeeRepository.findByRoles_RoleName("SUPPORT");

        // Populate transient fields
        for (Employee employee : employees) {
            employee.setOpenTickets(ticketRepository.countAllOpenTicketFor(employee));
            employee.setTotalTickets(ticketRepository.countByHandledBy(employee));
            employee.setFullSatisfaction(ticketRepository.countFullSatisfactionRatingByEmployee(employee));
        }

        return employees;
    }



}
