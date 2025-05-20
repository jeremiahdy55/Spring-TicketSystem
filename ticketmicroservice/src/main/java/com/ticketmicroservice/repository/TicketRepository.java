package com.ticketmicroservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticketmicroservice.domain.Ticket;
import com.ticketmicroservice.domain.TicketStatus;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>{
    List<Ticket> findByAssignee_Id(Long assigneeId);
    List<Ticket> findByStatusInAndAssignee_Id(List<TicketStatus> statuses, Long assigneeId);
    List<Ticket> findByStatusInAndCreatedBy_Id(List<TicketStatus> statuses, Long createdById);
    List<Ticket> findByStatusIn(List<TicketStatus> statuses);
    List<Ticket> findByCreatedBy_Id(Long createdById);
}
