package com.alhashim.oneIT.config;


import com.alhashim.oneIT.models.Role;
import com.alhashim.oneIT.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RolesInitializer {

    @Autowired
    private RoleRepository roleRepository;


    @Bean
    public CommandLineRunner initializeRoles()
    {
        return args -> {


            //  SUPERADMIN role exists, in admin initializer


            // Check if USER role exists, if not create it
            Role userRole = roleRepository.findByRoleName("USER").orElse(null);
            if(userRole ==null){
                userRole = new Role();
                userRole.setRoleName("USER");
                userRole.setCanDelete(false);
                userRole.setCanEdit(false);
                userRole.setCanRead(true);
                userRole.setCanWrite(true);
                roleRepository.save(userRole);
                System.out.println("USER role created.");
            }

                       };

    }
}
