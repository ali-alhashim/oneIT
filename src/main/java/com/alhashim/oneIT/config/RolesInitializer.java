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


                            //  ADMIN role exists, in admin initializer


                            // Check if USER role exists, if not create it
                            Role userRole = roleRepository.findByRoleName("USER").orElse(null);
                            if(userRole ==null){
                                userRole = new Role();
                                userRole.setRoleName("USER");

                                roleRepository.save(userRole);
                                System.out.println("USER role created.");
                            }


                            // Check if ROLE_MANAGER role exists, if not create it
                            Role managerRole = roleRepository.findByRoleName("MANAGER").orElse(null);
                            if(managerRole ==null){
                                managerRole = new Role();
                                managerRole.setRoleName("MANAGER");

                                roleRepository.save(managerRole);
                                System.out.println("MANAGER role created.");
                            }


                            // Check if ROLE_HR role exists, if not create it
                            Role hrRole = roleRepository.findByRoleName("HR").orElse(null);
                            if(hrRole ==null){
                                hrRole = new Role();
                                hrRole.setRoleName("HR");

                                roleRepository.save(hrRole);
                                System.out.println("HR created.");
                            }



                            // Check if ROLE_SUPPORT role exists, if not create it
                            Role supportRole = roleRepository.findByRoleName("SUPPORT").orElse(null);
                            if(supportRole ==null){
                                supportRole = new Role();
                                supportRole.setRoleName("SUPPORT");

                                roleRepository.save(supportRole);
                                System.out.println("SUPPORT  created.");
                            }

                            // check if PROCUREMENT role exists if not create it
                               Role procurementRole = roleRepository.findByRoleName("PROCUREMENT").orElse(null);
                            if(procurementRole == null)
                            {
                                procurementRole = new Role();
                                procurementRole.setRoleName("PROCUREMENT");
                                roleRepository.save(procurementRole);
                                System.out.println("PROCUREMENT Role Created");
                            }

                          //MEDICAL
                           Role medicalRole = roleRepository.findByRoleName("MEDICAL").orElse(null);
                            if(medicalRole ==null)
                            {
                                medicalRole = new Role();
                                medicalRole.setRoleName("MEDICAL");
                                roleRepository.save(medicalRole);
                                System.out.println("MEDICAL Role Created");
                            }
                          //VEHICLE

                            Role vehicleRole = roleRepository.findByRoleName("VEHICLE").orElse(null);
                            if(vehicleRole ==null)
                            {
                                vehicleRole = new Role();
                                vehicleRole.setRoleName("VEHICLE");
                                roleRepository.save(vehicleRole);
                                System.out.println("VEHICLE Role Created");
                            }
                          //FINANCE

                        Role financeRole = roleRepository.findByRoleName("FINANCE").orElse(null);
                        if(financeRole ==null)
                        {
                            financeRole = new Role();
                            financeRole.setRoleName("FINANCE");
                            roleRepository.save(financeRole);
                            System.out.println("FINANCE Role Created");
                        }


                       };

    }
}
