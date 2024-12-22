package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Geolocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GeolocationRepository extends JpaRepository<Geolocation, Long> {

    Geolocation findByAreaName(String areaName);

    @Query("""
        SELECT g.areaName
        FROM Geolocation g
        WHERE :latitude BETWEEN LEAST(g.latitudeA, g.latitudeB, g.latitudeC, g.latitudeD)
                          AND GREATEST(g.latitudeA, g.latitudeB, g.latitudeC, g.latitudeD)
          AND :longitude BETWEEN LEAST(g.longitudeA, g.longitudeB, g.longitudeC, g.longitudeD)
                           AND GREATEST(g.longitudeA, g.longitudeB, g.longitudeC, g.longitudeD)
    """)
    Optional<Geolocation> findAreaNameByCoordinates(@Param("latitude") Double latitude, @Param("longitude") Double longitude);


    @Query("SELECT e FROM Geolocation e WHERE " +
            "LOWER(e.areaName) LIKE LOWER(CONCAT('%', :keyword, '%')) " )
    Page<Geolocation> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
