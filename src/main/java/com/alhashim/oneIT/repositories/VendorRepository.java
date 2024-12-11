package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
}
