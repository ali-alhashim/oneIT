package com.alhashim.oneIT.repositories;


import com.alhashim.oneIT.models.ShiftSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShiftScheduleRepository extends JpaRepository<ShiftSchedule, Long> {


    @Query("SELECT e FROM ShiftSchedule e WHERE " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))"
           )
    Page<ShiftSchedule> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
