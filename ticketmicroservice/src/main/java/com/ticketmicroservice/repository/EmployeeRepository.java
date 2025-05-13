package com.ticketmicroservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticketmicroservice.domain.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{

}
