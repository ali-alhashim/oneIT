package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Document;
import com.alhashim.oneIT.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByEmployeeId(Long employeeId); // Fetch documents by employee ID
    List<Document> findByEmployee(Employee employee);
    Optional<Document> findByFileName(String fileName);
}
