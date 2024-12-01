package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByEmployeeId(Long employeeId); // Fetch documents by employee ID
}
