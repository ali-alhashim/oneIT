package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Contact;
import com.alhashim.oneIT.models.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByVendor(Vendor vendor);
}
