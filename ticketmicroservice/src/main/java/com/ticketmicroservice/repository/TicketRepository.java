package com.ticketmicroservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticketmicroservice.domain.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>{

}
