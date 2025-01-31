package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Device;
import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByRequestedBy(Employee employee, Pageable pageable);

    List<Ticket> findByHandledBy(Employee employee);



    List<Ticket> findByDevice(Device device);

    List<Ticket> findByStatus(String status);


    @Query("SELECT e FROM Ticket e WHERE " +
            "LOWER(e.requestedBy.badgeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.requestedBy.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.requestedBy.arName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.requestedBy.workMobile) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.requestedBy.workEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Ticket> findByKeyword(@Param("keyword") String keyword, Pageable pageable);



    @Query("SELECT t FROM Ticket t WHERE t.requestedBy = :employee AND (t.subject LIKE %:keyword% OR t.description LIKE %:keyword%)")
    Page<Ticket> findByKeywordAndRequestedBy(@Param("keyword") String keyword, @Param("employee") Employee employee, Pageable pageable);



    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.handledBy = :employee AND t.satisfactionRating = 5")
    int countFullSatisfactionRatingByEmployee(@Param("employee") Employee employee);

    //count  All Ticket that status = In Progress for employee
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.handledBy = :employee AND t.status = 'In Progress'")
    int countAllOpenTicketFor(@Param("employee") Employee employee);

    //total Ticket
    int countByHandledBy(Employee employee);


}
