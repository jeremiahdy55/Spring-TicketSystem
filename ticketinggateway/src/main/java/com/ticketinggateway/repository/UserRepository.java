package com.ticketinggateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticketinggateway.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findByUserName(String userName);
}
