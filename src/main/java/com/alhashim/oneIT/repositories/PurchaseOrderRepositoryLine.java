package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepositoryLine extends JpaRepository<PurchaseOrderLine, Long> {

}
