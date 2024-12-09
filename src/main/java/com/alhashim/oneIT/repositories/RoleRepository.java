package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
     Optional<Role> findByRoleName(String roleName);

     Page<Employee> findByRoleName(String roleName, Pageable pageable);

}
