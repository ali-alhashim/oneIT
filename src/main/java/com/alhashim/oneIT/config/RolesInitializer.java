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


                            // Check if MANAGER role exists, if not create it
                            Role managerRole = roleRepository.findByRoleName("MANAGER").orElse(null);
                            if(managerRole ==null){
                                managerRole = new Role();
                                managerRole.setRoleName("MANAGER");
                                managerRole.setCanDelete(false);
                                managerRole.setCanEdit(false);
                                managerRole.setCanRead(true);
                                managerRole.setCanWrite(true);
                                roleRepository.save(managerRole);
                                System.out.println("MANAGER role created.");
                            }



                            // Check if SUPPORT role exists, if not create it
                            Role supportRole = roleRepository.findByRoleName("SUPPORT").orElse(null);
                            if(supportRole ==null){
                                supportRole = new Role();
                                supportRole.setRoleName("SUPPORT");
                                supportRole.setCanDelete(false);
                                supportRole.setCanEdit(false);
                                supportRole.setCanRead(true);
                                supportRole.setCanWrite(true);
                                roleRepository.save(supportRole);
                                System.out.println("SUPPORT role created.");
                            }

                       };

    }
}
