package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    public Optional<Role> findByRoleName(String roleName);
}
